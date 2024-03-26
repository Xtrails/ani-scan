/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import InstrumentType from './instrument-type.vue';
import InstrumentTypeService from './instrument-type.service';
import AlertService from '@/shared/alert/alert.service';

type InstrumentTypeComponentType = InstanceType<typeof InstrumentType>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('InstrumentType Management Component', () => {
    let instrumentTypeServiceStub: SinonStubbedInstance<InstrumentTypeService>;
    let mountOptions: MountingOptions<InstrumentTypeComponentType>['global'];

    beforeEach(() => {
      instrumentTypeServiceStub = sinon.createStubInstance<InstrumentTypeService>(InstrumentTypeService);
      instrumentTypeServiceStub.retrieve.resolves({ headers: {} });

      alertService = new AlertService({
        i18n: { t: vitest.fn() } as any,
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          bModal: bModalStub as any,
          'font-awesome-icon': true,
          'b-badge': true,
          'b-button': true,
          'router-link': true,
        },
        directives: {
          'b-modal': {},
        },
        provide: {
          alertService,
          instrumentTypeService: () => instrumentTypeServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        instrumentTypeServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        const wrapper = shallowMount(InstrumentType, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(instrumentTypeServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.instrumentTypes[0]).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
    describe('Handles', () => {
      let comp: InstrumentTypeComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(InstrumentType, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        instrumentTypeServiceStub.retrieve.reset();
        instrumentTypeServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        instrumentTypeServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: 123 });

        comp.removeInstrumentType();
        await comp.$nextTick(); // clear components

        // THEN
        expect(instrumentTypeServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(instrumentTypeServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
