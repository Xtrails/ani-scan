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
      // it('should find an element', async () => {
      //   const returnedFromService = Object.assign(
      //     {
      //       firstOperationDttm: dayjs(currentDate).format(DATE_TIME_FORMAT),
      //       lastOperationDttm: dayjs(currentDate).format(DATE_TIME_FORMAT),
      //     },
      //     elemDefault,
      //   );
      //   axiosStub.get.resolves({ data: returnedFromService });
      //
      //   return service.find(123).then(res => {
      //     expect(res).toMatchObject(elemDefault);
      //   });
      // });
      //
      // it('should not find an element', async () => {
      //   axiosStub.get.rejects(error);
      //   return service
      //     .find(123)
      //     .then()
      //     .catch(err => {
      //       expect(err).toMatchObject(error);
      //     });
      // });

      // it('should create a Robot', async () => {
      //   const returnedFromService = Object.assign(
      //     {
      //       id: 123,
      //       firstOperationDttm: dayjs(currentDate).format(DATE_TIME_FORMAT),
      //       lastOperationDttm: dayjs(currentDate).format(DATE_TIME_FORMAT),
      //     },
      //     elemDefault,
      //   );
      //   const expected = Object.assign(
      //     {
      //       firstOperationDttm: currentDate,
      //       lastOperationDttm: currentDate,
      //     },
      //     returnedFromService,
      //   );
      //
      //   axiosStub.post.resolves({ data: returnedFromService });
      //   return service.create({}).then(res => {
      //     expect(res).toMatchObject(expected);
      //   });
      // });
      //
      // it('should not create a Robot', async () => {
      //   axiosStub.post.rejects(error);
      //
      //   return service
      //     .create({})
      //     .then()
      //     .catch(err => {
      //       expect(err).toMatchObject(error);
      //     });
      // });

      // it('should update a Robot', async () => {
      //   const returnedFromService = Object.assign(
      //     {
      //       type: 'BBBBBB',
      //       lots: 'BBBBBB',
      //       period: 1,
      //       operationType: 'BBBBBB',
      //       operationCount: 1,
      //       firstOperationDttm: dayjs(currentDate).format(DATE_TIME_FORMAT),
      //       lastOperationDttm: dayjs(currentDate).format(DATE_TIME_FORMAT),
      //       lastPrice: 1,
      //       volumeByHour: 1,
      //     },
      //     elemDefault,
      //   );
      //
      //   const expected = Object.assign(
      //     {
      //       firstOperationDttm: currentDate,
      //       lastOperationDttm: currentDate,
      //     },
      //     returnedFromService,
      //   );
      //   axiosStub.put.resolves({ data: returnedFromService });
      //
      //   return service.update(expected).then(res => {
      //     expect(res).toMatchObject(expected);
      //   });
      // });
      //
      // it('should not update a Robot', async () => {
      //   axiosStub.put.rejects(error);
      //
      //   return service
      //     .update({})
      //     .then()
      //     .catch(err => {
      //       expect(err).toMatchObject(error);
      //     });
      // });

      // it('should partial update a Robot', async () => {
      //   const patchObject = Object.assign(
      //     {
      //       lots: 'BBBBBB',
      //       period: 1,
      //       operationType: 'BBBBBB',
      //       operationCount: 1,
      //       lastPrice: 1,
      //       volumeByHour: 1,
      //     },
      //     new Robot(),
      //   );
      //   const returnedFromService = Object.assign(patchObject, elemDefault);
      //
      //   const expected = Object.assign(
      //     {
      //       firstOperationDttm: currentDate,
      //       lastOperationDttm: currentDate,
      //     },
      //     returnedFromService,
      //   );
      //   axiosStub.patch.resolves({ data: returnedFromService });
      //
      //   return service.partialUpdate(patchObject).then(res => {
      //     expect(res).toMatchObject(expected);
      //   });
      // });
      //
      // it('should not partial update a Robot', async () => {
      //   axiosStub.patch.rejects(error);
      //
      //   return service
      //     .partialUpdate({})
      //     .then()
      //     .catch(err => {
      //       expect(err).toMatchObject(error);
      //     });
      // });

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

      // it('should delete a Robot', async () => {
      //   axiosStub.delete.resolves({ ok: true });
      //   return service.delete(123).then(res => {
      //     expect(res.ok).toBeTruthy();
      //   });
      // });
      //
      // it('should not delete a Robot', async () => {
      //   axiosStub.delete.rejects(error);
      //
      //   return service
      //     .delete(123)
      //     .then()
      //     .catch(err => {
      //       expect(err).toMatchObject(error);
      //     });
      // });
    });
  });
});
