/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import InstrumentUpdate from './instrument-update.vue';
import InstrumentService from './instrument.service';
import AlertService from '@/shared/alert/alert.service';

type InstrumentUpdateComponentType = InstanceType<typeof InstrumentUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const instrumentSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<InstrumentUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('Instrument Management Update Component', () => {
    let comp: InstrumentUpdateComponentType;
    let instrumentServiceStub: SinonStubbedInstance<InstrumentService>;

    beforeEach(() => {
      route = {};
      instrumentServiceStub = sinon.createStubInstance<InstrumentService>(InstrumentService);
      instrumentServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          instrumentService: () => instrumentServiceStub,
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const wrapper = shallowMount(InstrumentUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.instrument = instrumentSample;
        instrumentServiceStub.update.resolves(instrumentSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(instrumentServiceStub.update.calledWith(instrumentSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        instrumentServiceStub.create.resolves(entity);
        const wrapper = shallowMount(InstrumentUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.instrument = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(instrumentServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        instrumentServiceStub.find.resolves(instrumentSample);
        instrumentServiceStub.retrieve.resolves([instrumentSample]);

        // WHEN
        route = {
          params: {
            instrumentId: '' + instrumentSample.id,
          },
        };
        const wrapper = shallowMount(InstrumentUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.instrument).toMatchObject(instrumentSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        instrumentServiceStub.find.resolves(instrumentSample);
        const wrapper = shallowMount(InstrumentUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
