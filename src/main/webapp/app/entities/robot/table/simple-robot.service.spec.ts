/* tslint:disable max-line-length */
import axios from 'axios';
import sinon from 'sinon';
import dayjs from 'dayjs';

import { DATE_TIME_FORMAT } from '@/shared/composables/date-format';
import { Robot } from '@/shared/model/robot.model';
import SimpleRobotService from './simple-robot.service';

const error = {
  response: {
    status: null,
    data: {
      type: null,
    },
  },
};

const axiosStub = {
  get: sinon.stub(axios, 'get'),
  post: sinon.stub(axios, 'post'),
  put: sinon.stub(axios, 'put'),
  patch: sinon.stub(axios, 'patch'),
  delete: sinon.stub(axios, 'delete'),
};

describe('Service Tests', () => {
  describe('Robot Service', () => {
    let service: SimpleRobotService;
    let elemDefault;
    let currentDate: Date;

    beforeEach(() => {
      service = new SimpleRobotService();
      currentDate = new Date();
      elemDefault = new Robot(123, 'SIMPLE', 'AAAAAAA', 0, 'BUY', 0, currentDate, currentDate, 0, 0);
    });

    describe('Service methods', () => {
      it('should return a list of Robot', async () => {
        const returnedFromService = Object.assign(
          {
            type: 'BBBBBB',
            lots: 'BBBBBB',
            period: 1,
            operationType: 'BBBBBB',
            operationCount: 1,
            firstOperationDttm: dayjs(currentDate).format(DATE_TIME_FORMAT),
            lastOperationDttm: dayjs(currentDate).format(DATE_TIME_FORMAT),
            lastPrice: 1,
            volumeByHour: 1,
          },
          elemDefault,
        );
        const expected = Object.assign(
          {
            firstOperationDttm: currentDate,
            lastOperationDttm: currentDate,
          },
          returnedFromService,
        );
        axiosStub.get.resolves([returnedFromService]);
        return service.retrieve({ sort: {}, page: 0, size: 10 }).then(res => {
          expect(res).toContainEqual(expected);
        });
      });

      it('should not return a list of Robot', async () => {
        axiosStub.get.rejects(error);

        return service
          .retrieve()
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });
    });
  });
});
