import { defineComponent, inject, onMounted, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';

import InstrumentService from './instrument.service';
import { type IInstrument } from '@/shared/model/instrument.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'Instrument',
  setup() {
    const { t: t$ } = useI18n();
    const instrumentService = inject('instrumentService', () => new InstrumentService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const instruments: Ref<IInstrument[]> = ref([]);

    const isFetching = ref(false);

    const clear = () => {};

    const retrieveInstruments = async () => {
      isFetching.value = true;
      try {
        const res = await instrumentService().retrieve();
        instruments.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveInstruments();
    };

    onMounted(async () => {
      await retrieveInstruments();
    });

    const removeId: Ref<number> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: IInstrument) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeInstrument = async () => {
      try {
        await instrumentService().delete(removeId.value);
        const message = t$('aniScanApp.instrument.deleted', { param: removeId.value }).toString();
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveInstruments();
        closeDialog();
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    return {
      instruments,
      handleSyncList,
      isFetching,
      retrieveInstruments,
      clear,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeInstrument,
      t$,
    };
  },
});
