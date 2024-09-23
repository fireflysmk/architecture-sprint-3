import http from 'node:http';
import { Client } from 'pg';
import {Consumer, Kafka} from 'kafkajs';

const port = process.env['PORT'] ?? 80;
const kafkaServer = process.env['KAFKA'];
const dbHost = process.env['POSTGRES_HOST'];
const dbName = process.env['POSTGRES_DB'];
const dbUser = process.env['POSTGRES_USER'];
const dbPassword = process.env['POSTGRES_PASSWORD'];
const dbPort = process.env['POSTGRES_PORT'];

if (!kafkaServer) {
    process.exit(1);
}
if (!dbHost || !dbName || !dbUser || !dbPassword || !dbPort) {
    process.exit(1);
}

const kafka = new Kafka({
    clientId: 'devices',
    brokers: [kafkaServer],
});

type Router = {
    path: RegExp;
    method: string;
    listener: http.RequestListener;
};

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

class DB {
    #client: Client;

    constructor() {
        this.#client = new Client({
            user: dbUser,
            password: dbPassword,
            host: dbHost,
            port: Number(dbPort),
            database: dbName,
        });
    }

    connect() {
        return this.#client.connect();
    }

    selectDevices(user_id: string) {
        return this.#client.query(`
            SELECT
                devices.id,
                devices.serial_number,
                devices.name,
                devices.current_parameters,
                devices.status,
                devices.house_id,
                devices.module_id,
                device_types.type
            FROM devices
            JOIN device_types ON (device_types.id = devices.type_id)
            WHERE user_id = $1
        `, [user_id]);
    }

    selectDevice(id: string) {
        return this.#client.query(`
            SELECT
                devices.id,
                devices.serial_number,
                devices.name,
                devices.current_parameters,
                devices.status,
                devices.house_id,
                devices.module_id,
                device_types.type
            FROM devices
            JOIN device_types ON (device_types.id = devices.type_id)
            WHERE id = $1
        `, [id]);
    }

    selectDeviceType(type: string) {
        return this.#client.query(`
            SELECT
                *
            FROM device_types
            WHERE type = $1
        `, [type]);
    }

    insertDevice(data: AddingDeviceData) {
        return this.selectDeviceType(data.type)
            .then((result) => {
                const deviceType = result.rows[0];
                if (!deviceType) {
                    return;
                }
                return this.#client.query(`
                    INSERT INTO devices
                        (serial_number, name, status, type_id, current_parameters, user_id)
                    VALUES ($1, $2, $3, $4, $5, $6)
                `, [data.serial_number, data.name, false, deviceType.id, deviceType.default_parameters, data.user_id]);
            });
    }

    updateDevice(data: EditingDeviceData) {
        return this.selectDevice(data.device_id)
            .then((result) => {
                const device = result.rows[0];
                if (!device) {
                    return;
                }
                return this.#client.query(`
                    UPDATE devices SET
                        name = $1,
                        status = $2,
                        current_parameters = $3,
                        house_id = $4,
                        module_id = $5
                    WHERE id = $6
                `, [data.name ?? device.name, data.status ?? device.status, data.current_parameters ?? device.current_parameters, data.house_id ?? device.house_id, data.module_id ?? device.module_id, data.device_id]);
            });
    }

    deleteDevice(id: string) {
        return this.#client.query(`DELETE FROM devices WHERE id = $1`, [id]);
    }

    selectModules(user_id: string) {
        return this.#client.query(`
            SELECT
                modules.id,
                modules.serial_number,
                modules.name,
                modules.status,
                modules.house_id
            FROM modules
            WHERE user_id = $1
        `, [user_id]);
    }

    selectModule(id: string) {
        return this.#client.query(`
            SELECT
                modules.id,
                modules.serial_number,
                modules.name,
                modules.status,
                modules.house_id
            FROM modules
            WHERE id = $1
        `, [id]);
    }

    insertModule(data: AddingModuleData) {
        return this.#client.query(`
            INSERT INTO modules
                (serial_number, name, status, user_id)
            VALUES ($1, $2, $3, $4)
        `, [data.serial_number, data.name, false, data.user_id]);
    }

    updateModule(data: EditingModuleData) {
        return this.selectModule(data.module_id)
            .then((result) => {
                const module = result.rows[0];
                if (!module) {
                    return;
                }
                return this.#client.query(`
                    UPDATE modules SET
                        name = $1,
                        status = $2,
                        house_id = $3
                    WHERE id = $4
                `, [data.name ?? module.name, data.status ?? module.status, data.house_id ?? module.house_id, data.module_id]);
            });
    }

    deleteModule(id: string) {
        return this.#client.query(`DELETE FROM modules WHERE id = $1`, [id]);
    }
}

class QueueConsumer {
    #consumer: Consumer;

    constructor(kafka: Kafka) {
        this.#consumer = kafka.consumer({groupId: 'devices'});
    }

    connect() {
        return this.#consumer.connect()
            .then(() => {
                console.log('connected');
                return Promise.all([
                    this.#consumer.subscribe({topic: 'devices', fromBeginning: true}),
                    this.#consumer.subscribe({topic: 'modules', fromBeginning: true}),
                ]);
            })
            .then(() => {
                return this.#consumer.run({
                    eachMessage: async ({message}) => {
                        const json = JSON.parse(message.value?.toString() ?? '{}');
                        console.log(json);
                        switch (json.message_type) {
                            case 'add-device': {
                                db.insertDevice(json);
                                break;
                            }
                            case 'update-device': {
                                db.updateDevice(json);
                                break;
                            }
                            case 'delete-device': {
                                db.deleteDevice(json.device_id);
                                break;
                            }
                            case 'add-module': {
                                db.insertModule(json);
                                break;
                            }
                            case 'update-module': {
                                db.updateModule(json);
                                break;
                            }
                            case 'delete-module': {
                                db.deleteModule(json.module_id);
                                break;
                            }
                        }
                    },
                });
            });
    }
}

const queueConsumer = new QueueConsumer(kafka);
const db = new DB();

const routers: Router[] = [
    {
        path: /devices\/.+/,
        method: 'GET',
        listener: async (req, res) => {
            const device_id = req.url?.match(/devices\/(.+)/)?.[1];
            if (!device_id) {
                return res.writeHead(400).end();
            }
            const result = await db.selectDevice(device_id);
            if (!result.rows[0]) {
                return res.writeHead(400).end();
            }

            return res
                .writeHead(200, {'Content-Type': 'application/json'})
                .end(JSON.stringify(result.rows[0]));
        },
    },
    {
        path: /devices/,
        method: 'GET',
        listener: async (req, res) => {
            const url = new URL(`http://${process.env.HOST ?? 'localhost'}${req.url}`);
            const user_id = url.searchParams.get('user_id');
            if (!user_id) {
                return res.writeHead(400, {'Content-Type': 'text/plain'}).end();
            }
            const result = await db.selectDevices(user_id);

            return res
                .writeHead(200, {'Content-Type': 'application/json'})
                .end(JSON.stringify(result.rows));
        },
    },
    {
        path: /modules\/.+/,
        method: 'GET',
        listener: async (req, res) => {
            const module_id = req.url?.match(/modules\/(.+)/)?.[1];
            if (!module_id) {
                return res.writeHead(400).end();
            }
            const result = await db.selectModule(module_id);
            if (!result.rows[0]) {
                return res.writeHead(400).end();
            }

            return res
                .writeHead(200, {'Content-Type': 'application/json'})
                .end(JSON.stringify(result.rows[0]));
        },
    },
    {
        path: /modules/,
        method: 'GET',
        listener: async (req, res) => {
            const url = new URL(`http://${process.env.HOST ?? 'localhost'}${req.url}`);
            const user_id = url.searchParams.get('user_id');
            if (!user_id) {
                return res.writeHead(400, {'Content-Type': 'text/plain'}).end();
            }
            const result = await db.selectModules(user_id);

            return res
                .writeHead(200, {'Content-Type': 'application/json'})
                .end(JSON.stringify(result.rows));
        },
    },
];

const server = http.createServer((req, res) => {
    console.log(req.method, req.url);
    const router = routers.find((router) => {
        return router.method === req.method && router.path.test(req.url ?? '');
    });
    if (router) {
        router.listener(req, res);
    } else {
        res.writeHead(404, {'Content-Type': 'text/plain'}).end('Not found');
    }
});

server.listen(port);
queueConsumer.connect();
db.connect();
