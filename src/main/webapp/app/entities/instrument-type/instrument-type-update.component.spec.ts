/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import InstrumentTypeUpdate from './instrument-type-update.vue';
import InstrumentTypeService from './instrument-type.service';
import AlertService from '@/shared/alert/alert.service';

type InstrumentTypeUpdateComponentType = InstanceType<typeof InstrumentTypeUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const instrumentTypeSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<InstrumentTypeUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('InstrumentType Management Update Component', () => {
    let comp: InstrumentTypeUpdateComponentType;
    let instrumentTypeServiceStub: SinonStubbedInstance<InstrumentTypeService>;

    beforeEach(() => {
      route = {};
      instrumentTypeServiceStub = sinon.createStubInstance<InstrumentTypeService>(InstrumentTypeService);
      instrumentTypeServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          instrumentTypeService: () => instrumentTypeServiceStub,
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const wrapper = shallowMount(InstrumentTypeUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.instrumentType = instrumentTypeSample;
        instrumentTypeServiceStub.update.resolves(instrumentTypeSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(instrumentTypeServiceStub.update.calledWith(instrumentTypeSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        instrumentTypeServiceStub.create.resolves(entity);
        const wrapper = shallowMount(InstrumentTypeUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.instrumentType = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(instrumentTypeServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        instrumentTypeServiceStub.find.resolves(instrumentTypeSample);
        instrumentTypeServiceStub.retrieve.resolves([instrumentTypeSample]);

        // WHEN
        route = {
          params: {
            instrumentTypeId: '' + instrumentTypeSample.id,
          },
        };
        const wrapper = shallowMount(InstrumentTypeUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.instrumentType).toMatchObject(instrumentTypeSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        instrumentTypeServiceStub.find.resolves(instrumentTypeSample);
        const wrapper = shallowMount(InstrumentTypeUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
