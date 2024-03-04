/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import InstrumentDetails from './instrument-details.vue';
import InstrumentService from './instrument.service';
import AlertService from '@/shared/alert/alert.service';

type InstrumentDetailsComponentType = InstanceType<typeof InstrumentDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const instrumentSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('Instrument Management Detail Component', () => {
    let instrumentServiceStub: SinonStubbedInstance<InstrumentService>;
    let mountOptions: MountingOptions<InstrumentDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      instrumentServiceStub = sinon.createStubInstance<InstrumentService>(InstrumentService);

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
          instrumentService: () => instrumentServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        instrumentServiceStub.find.resolves(instrumentSample);
        route = {
          params: {
            instrumentId: '' + 123,
          },
        };
        const wrapper = shallowMount(InstrumentDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.instrument).toMatchObject(instrumentSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        instrumentServiceStub.find.resolves(instrumentSample);
        const wrapper = shallowMount(InstrumentDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
