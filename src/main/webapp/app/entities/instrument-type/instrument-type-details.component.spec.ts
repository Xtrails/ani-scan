/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import InstrumentTypeDetails from './instrument-type-details.vue';
import InstrumentTypeService from './instrument-type.service';
import AlertService from '@/shared/alert/alert.service';

type InstrumentTypeDetailsComponentType = InstanceType<typeof InstrumentTypeDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const instrumentTypeSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('InstrumentType Management Detail Component', () => {
    let instrumentTypeServiceStub: SinonStubbedInstance<InstrumentTypeService>;
    let mountOptions: MountingOptions<InstrumentTypeDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      instrumentTypeServiceStub = sinon.createStubInstance<InstrumentTypeService>(InstrumentTypeService);

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
          instrumentTypeService: () => instrumentTypeServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        instrumentTypeServiceStub.find.resolves(instrumentTypeSample);
        route = {
          params: {
            instrumentTypeId: '' + 123,
          },
        };
        const wrapper = shallowMount(InstrumentTypeDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.instrumentType).toMatchObject(instrumentTypeSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        instrumentTypeServiceStub.find.resolves(instrumentTypeSample);
        const wrapper = shallowMount(InstrumentTypeDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
