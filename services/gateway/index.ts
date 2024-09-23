import http, {request} from 'node:http';
import {Kafka, Producer} from 'kafkajs';

const port = process.env['PORT'] ?? 80;
const deviceServer = process.env['DEVICE_SERVER']?.split(':');
const telemetryServer = process.env['TELEMETRY_SERVER']?.split(':');
const kafkaServer = process.env['KAFKA'];

if (!deviceServer || deviceServer.length !== 2) {
    process.exit(1);
}
if (!telemetryServer || telemetryServer.length !== 2) {
    process.exit(1);
}
if (!kafkaServer) {
    process.exit(1);
}

const kafka = new Kafka({
    clientId: 'gateway',
    brokers: [kafkaServer],
});

const corsHeaders = {
    'Access-Control-Allow-Origin': `http://localhost:8080`, // for swagger
    'Access-Control-Allow-Methods': 'POST, GET, OPTIONS, PATCH, DELETE',
    'Access-Control-Allow-Headers': 'Content-Type',
};

type Router = {
    path: RegExp;
    method: string;
    listener: http.RequestListener;
};

function forwardAsIs(hostname: string, port = '80'):
    (req: http.IncomingMessage, res: http.ServerResponse<http.IncomingMessage>) => void {
    return (req, res) => {
        const url = new URL(`http://${process.env.HOST ?? 'localhost'}${req.url}`);
        url.hostname = hostname;
        url.port = port;

        console.log(`Redirect to: ${hostname}:${port}`);
        const proxyRequest = request(url, (response) => {
            console.log(`Pipe to: ${hostname}:${port}`);
            res.writeHead(response.statusCode ?? 200, {...response.headers, ...corsHeaders});
            response.pipe(res);
        });
        proxyRequest.on('error', (e) => {
            console.error(`Problem with request: ${e.message}`);
            return res.writeHead(500, corsHeaders).end();
        });
        req.pipe(proxyRequest);
    }
}

function readBody(req: http.IncomingMessage): Promise<string> {
    return new Promise((res, rej) => {
        const chunks: string[] = [];
        req.setEncoding('utf8');
        req.on('data', (data) => {
            chunks.push(data);
        });
        req.on('end', () => {
            res(chunks.join(''));
        });
        req.on('error', (err) => {
            rej(err);
        });
    });
}

type AddingDeviceData = {
    serial_number: string;
    name: string;
    type: string;
    user_id: string;
}

type EditingDeviceData = {
    device_id: string;
    name?: string;
    current_parameters?: string;
    status?: boolean;
    house_id?: string;
    module_id?: string;
}

type AddingModuleData = {
    serial_number: string;
    name: string;
    user_id: string;
}

type EditingModuleData = {
    module_id: string;
    name?: string;
    status?: boolean;
    house_id?: string;
}

type AddingTelemetryData = {
    indications: string;
    device_id: string;
}

class QueueProducer {
    #producer: Producer;

    constructor(kafka: Kafka) {
        this.#producer = kafka.producer();
        this.#producer.connect();
    }

    publishAddDevice(data: AddingDeviceData) {
        this.#producer.send({
            topic: 'devices',
            messages: [{
                value: JSON.stringify({
                    message_type: 'add-device',
                    ...data,
                }),
            }],
        });
    }
    publishEditDevice(data: EditingDeviceData) {
        this.#producer.send({
            topic: 'devices',
            messages: [{
                value: JSON.stringify({
                    message_type: 'update-device',
                    ...data,
                }),
            }],
        });
    }
    publishDeleteDevice(id: string) {
        this.#producer.send({
            topic: 'devices',
            messages: [{
                value: JSON.stringify({
                    message_type: 'delete-device',
                    device_id: id,
                }),
            }],
        });
    }
    publishAddModule(data: AddingModuleData) {
        this.#producer.send({
            topic: 'modules',
            messages: [{
                value: JSON.stringify({
                    message_type: 'add-module',
                    ...data,
                }),
            }],
        });
    }
    publishEditModule(data: EditingModuleData) {
        this.#producer.send({
            topic: 'modules',
            messages: [{
                value: JSON.stringify({
                    message_type: 'update-module',
                    ...data,
                }),
            }],
        });
    }
    publishDeleteModule(id: string) {
        this.#producer.send({
            topic: 'modules',
            messages: [{
                value: JSON.stringify({
                    message_type: 'delete-module',
                    module_id: id,
                }),
            }],
        });
    }
    publishAddTelemetry(data: AddingTelemetryData) {
        this.#producer.send({
            topic: 'telemetries',
            messages: [{
                value: JSON.stringify({
                    message_type: 'add-telemetry',
                    ...data,
                }),
            }],
        });
    }
}

const queueProducer = new QueueProducer(kafka);

const routers: Router[] = [
    {
        path: /devices\/.+\/telemetries\/latest/,
        method: 'GET',
        listener: forwardAsIs(telemetryServer[0], telemetryServer[1]),
    },
    {
        path: /devices\/.+\/telemetries/,
        method: 'GET',
        listener: forwardAsIs(telemetryServer[0], telemetryServer[1]),
    },
    {
        path: /devices\/.+\/telemetries/,
        method: 'POST',
        listener: async (req, res) => {
            const device_id = req.url?.match(/devices\/(.+)\/telemetries/)?.[1];
            if (!device_id) {
                return res.writeHead(400).end();
            }
            const body = await readBody(req);
            const json = JSON.parse(body);
            queueProducer.publishAddTelemetry({
                device_id,
                indications: json.indications,
            })
            return res.writeHead(204, {'Content-Type': 'text/plain', ...corsHeaders}).end();
        },
    },
    {
        path: /devices\/.+/,
        method: 'GET',
        listener: forwardAsIs(deviceServer[0], deviceServer[1]),
    },
    {
        path: /devices\/.+/,
        method: 'DELETE',
        listener: (req, res) => {
            const device_id = req.url?.match(/devices\/(.+)/)?.[1];
            if (!device_id) {
                return res.writeHead(400).end();
            }
            queueProducer.publishDeleteDevice(device_id);
            res.writeHead(204, {'Content-Type': 'text/plain', ...corsHeaders}).end();
        },
    },
    {
        path: /devices\/.+/,
        method: 'PATCH',
        listener: async (req, res) => {
            const device_id = req.url?.match(/devices\/(.+)/)?.[1];
            if (!device_id) {
                return res.writeHead(400).end();
            }
            const body = await readBody(req);
            const json = JSON.parse(body);
            queueProducer.publishEditDevice({
                device_id,
                current_parameters: json.current_parameters,
                house_id: json.house_id,
                module_id: json.module_id,
                name: json.name,
                status: json.status,
            })
            return res.writeHead(204, {'Content-Type': 'text/plain', ...corsHeaders}).end();
        },
    },
    {
        path: /devices/,
        method: 'GET',
        listener: forwardAsIs(deviceServer[0], deviceServer[1]),
    },
    {
        path: /devices/,
        method: 'POST',
        listener: async (req, res) => {
            const body = await readBody(req);
            const json = JSON.parse(body);
            queueProducer.publishAddDevice({
                user_id: json.user_id,
                serial_number: json.serial_number,
                name: json.name,
                type: json.type,
            })
            return res.writeHead(204, {'Content-Type': 'text/plain', ...corsHeaders}).end();
        },
    },
    {
        path: /modules\/.+/,
        method: 'GET',
        listener: forwardAsIs(deviceServer[0], deviceServer[1]),
    },
    {
        path: /modules\/.+/,
        method: 'DELETE',
        listener: (req, res) => {
            const module_id = req.url?.match(/modules\/(.+)/)?.[1];
            if (!module_id) {
                return res.writeHead(400).end();
            }
            queueProducer.publishDeleteModule(module_id);
            res.writeHead(204, {'Content-Type': 'text/plain', ...corsHeaders}).end();
        },
    },
    {
        path: /modules\/.+/,
        method: 'PATCH',
        listener: async (req, res) => {
            const module_id = req.url?.match(/modules\/(.+)/)?.[1];
            if (!module_id) {
                return res.writeHead(400).end();
            }
            const body = await readBody(req);
            const json = JSON.parse(body);
            queueProducer.publishEditModule({
                module_id,
                house_id: json.house_id,
                name: json.name,
                status: json.status,
            })
            return res.writeHead(204, {'Content-Type': 'text/plain', ...corsHeaders}).end();
        },
    },
    {
        path: /modules/,
        method: 'GET',
        listener: forwardAsIs(deviceServer[0], deviceServer[1]),
    },
    {
        path: /modules/,
        method: 'POST',
        listener: async (req, res) => {
            const body = await readBody(req);
            const json = JSON.parse(body);
            queueProducer.publishAddModule({
                user_id: json.user_id,
                serial_number: json.serial_number,
                name: json.name,
            })
            return res.writeHead(204, {'Content-Type': 'text/plain', ...corsHeaders}).end();
        },
    },
];

const server = http.createServer((req, res) => {
    console.log(req.method, req.url);
    if (req.method === 'OPTIONS') {
        return res.writeHead(200, corsHeaders).end();
    }
    const router = routers.find((router) => {
        return router.method === req.method && router.path.test(req.url ?? '');
    });
    if (router) {
        router.listener(req, res);
    } else {
        res
            .writeHead(404, {
                'Content-Type': 'text/plain'
            })
            .end('Not found');
    }
});

server.listen(port);
