import http from 'k6/http';
import { check, sleep } from 'k6';

function randomIntBetween(min, max) {
    return Math.floor(Math.random() * (max - min + 1) + min);
}

export const options = {
    stages: [
        { duration: '30s', target: 100 },
        { duration: '1m', target: 500 },
        { duration: '30s', target: 1000 },
        { duration: '30s', target: 2000 },
        { duration: '30s', target: 0 },
    ],
    thresholds: {
        http_req_failed: ['rate<0.01'],
        http_req_duration: ['p(95)<500'],
    },
};

const BASE_URL = 'http://host.docker.internal:8080';

export default function () {
    const lat = randomIntBetween(37400, 37600) / 1000;
    const lng = randomIntBetween(126800, 127100) / 1000;
    const range = 0.07;

    const res = http.get(`${BASE_URL}/memories/map?lat=${lat}&lng=${lng}&range=${range}`);

    check(res, {
        'status is 200': (r) => r.status === 200,
    });

    sleep(1);
}
