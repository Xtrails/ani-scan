/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import dayjs from 'dayjs';
import RobotUpdate from './robot-update.vue';
import RobotService from './robot.service';
import { DATE_TIME_LONG_FORMAT } from '@/shared/composables/date-format';
import AlertService from '@/shared/alert/alert.service';

import InstrumentService from '@/entities/instrument/instrument.service';

type RobotUpdateComponentType = InstanceType<typeof RobotUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const robotSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<RobotUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('Robot Management Update Component', () => {
    let comp: RobotUpdateComponentType;
    let robotServiceStub: SinonStubbedInstance<RobotService>;

    beforeEach(() => {
      route = {};
      robotServiceStub = sinon.createStubInstance<RobotService>(RobotService);
      robotServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

      alertService = new AlertService({
        i18n: { t: vitest.fn() } as any,
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          'font-awesome-icon': true,
          'b-input-group': true,
          'b-input-group-prepend': true,
          'b-form-datepicker': true,
          'b-form-input': true,
        },
        provide: {
          alertService,
          robotService: () => robotServiceStub,
          instrumentService: () =>
            sinon.createStubInstance<InstrumentService>(InstrumentService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('load', () => {
      beforeEach(() => {
        const wrapper = shallowMount(RobotUpdate, { global: mountOptions });
        comp = wrapper.vm;
      });
      it('Should convert date from string', () => {
        // GIVEN
        const date = new Date('2019-10-15T11:42:02Z');

        // WHEN
        const convertedDate = comp.convertDateTimeFromServer(date);

        // THEN
        expect(convertedDate).toEqual(dayjs(date).format(DATE_TIME_LONG_FORMAT));
      });

      it('Should not convert date if date is not present', () => {
        expect(comp.convertDateTimeFromServer(null)).toBeNull();
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const wrapper = shallowMount(RobotUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.robot = robotSample;
        robotServiceStub.update.resolves(robotSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(robotServiceStub.update.calledWith(robotSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        robotServiceStub.create.resolves(entity);
        const wrapper = shallowMount(RobotUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.robot = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(robotServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        robotServiceStub.find.resolves(robotSample);
        robotServiceStub.retrieve.resolves([robotSample]);

        // WHEN
        route = {
          params: {
            robotId: '' + robotSample.id,
          },
        };
        const wrapper = shallowMount(RobotUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.robot).toMatchObject(robotSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        robotServiceStub.find.resolves(robotSample);
        const wrapper = shallowMount(RobotUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
