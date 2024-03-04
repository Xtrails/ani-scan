/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import Instrument from './instrument.vue';
import InstrumentService from './instrument.service';
import AlertService from '@/shared/alert/alert.service';

type InstrumentComponentType = InstanceType<typeof Instrument>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('Instrument Management Component', () => {
    let instrumentServiceStub: SinonStubbedInstance<InstrumentService>;
    let mountOptions: MountingOptions<InstrumentComponentType>['global'];

    beforeEach(() => {
      instrumentServiceStub = sinon.createStubInstance<InstrumentService>(InstrumentService);
      instrumentServiceStub.retrieve.resolves({ headers: {} });

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
          instrumentService: () => instrumentServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        instrumentServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        const wrapper = shallowMount(Instrument, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(instrumentServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.instruments[0]).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
    describe('Handles', () => {
      let comp: InstrumentComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(Instrument, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        instrumentServiceStub.retrieve.reset();
        instrumentServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        instrumentServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: 123 });

        comp.removeInstrument();
        await comp.$nextTick(); // clear components

        // THEN
        expect(instrumentServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(instrumentServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
