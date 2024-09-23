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

type AddingTelemetryData = {
    indications: string;
    device_id: string;
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

    selectTelemetries(device_id: string) {
        return this.#client.query(`
            SELECT
                *
            FROM telemetries
            WHERE device_id = $1
            ORDER BY created_at DESC
        `, [device_id]);
    }

    selectLatestTelemetry(device_id: string) {
        return this.#client.query(`
            SELECT
                *
            FROM telemetries
            WHERE device_id = $1
            ORDER BY created_at DESC
            LIMIT 1
        `, [device_id]);
    }

    insertTelemetry(data: AddingTelemetryData) {
        return this.#client.query(`
            INSERT INTO telemetries
                (device_id, indications, created_at)
            VALUES ($1, $2, $3)
        `, [data.device_id, data.indications, Date.now()]);
    }
}

class QueueConsumer {
    #consumer: Consumer;

    constructor(kafka: Kafka) {
        this.#consumer = kafka.consumer({groupId: 'telemetries'});
    }

    connect() {
        return this.#consumer.connect()
            .then(() => {
                console.log('connected');
                return this.#consumer.subscribe({topic: 'telemetries', fromBeginning: true});
            })
            .then(() => {
                return this.#consumer.run({
                    eachMessage: async ({message}) => {
                        const json = JSON.parse(message.value?.toString() ?? '{}');
                        console.log(json);
                        switch (json.message_type) {
                            case 'add-telemetry': {
                                db.insertTelemetry(json);
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
        path: /devices\/.+\/telemetries\/latest/,
        method: 'GET',
        listener: async (req, res) => {
            const device_id = req.url?.match(/devices\/(.+)\/telemetries/)?.[1];
            if (!device_id) {
                return res.writeHead(400).end();
            }
            const result = await db.selectLatestTelemetry(device_id);

            return res
                .writeHead(200, {'Content-Type': 'application/json'})
                .end(JSON.stringify(result.rows[0]));
        },
    },
    {
        path: /devices\/.+\/telemetries/,
        method: 'GET',
        listener: async (req, res) => {
            const device_id = req.url?.match(/devices\/(.+)\/telemetries/)?.[1];
            if (!device_id) {
                return res.writeHead(400).end();
            }
            const result = await db.selectTelemetries(device_id);

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
