import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    vus: 10,
    duration: '30s',
};

const API_URL = 'http://host.docker.internal:8080/memories?memberId=1&page=0&size=10';

export default function () {
    const res = http.get(API_URL);

    check(res, {
        'status is 200 or 404': (r) => r.status === 200 || r.status === 404,
    });

    sleep(1);
}
