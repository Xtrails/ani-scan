/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import RobotDetails from './robot-details.vue';
import RobotService from './robot.service';
import AlertService from '@/shared/alert/alert.service';

type RobotDetailsComponentType = InstanceType<typeof RobotDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const robotSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('Robot Management Detail Component', () => {
    let robotServiceStub: SinonStubbedInstance<RobotService>;
    let mountOptions: MountingOptions<RobotDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      robotServiceStub = sinon.createStubInstance<RobotService>(RobotService);

      alertService = new AlertService({
        i18n: { t: vitest.fn() } as any,
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          'font-awesome-icon': true,
          'router-link': true,
        },
        provide: {
          alertService,
          robotService: () => robotServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        robotServiceStub.find.resolves(robotSample);
        route = {
          params: {
            robotId: '' + 123,
          },
        };
        const wrapper = shallowMount(RobotDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.robot).toMatchObject(robotSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        robotServiceStub.find.resolves(robotSample);
        const wrapper = shallowMount(RobotDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
