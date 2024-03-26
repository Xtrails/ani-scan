import { defineComponent, inject, onMounted, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';

import InstrumentTypeService from './instrument-type.service';
import { type IInstrumentType } from '@/shared/model/instrument-type.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'InstrumentType',
  setup() {
    const { t: t$ } = useI18n();
    const instrumentTypeService = inject('instrumentTypeService', () => new InstrumentTypeService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const instrumentTypes: Ref<IInstrumentType[]> = ref([]);

    const isFetching = ref(false);

    const clear = () => {};

    const retrieveInstrumentTypes = async () => {
      isFetching.value = true;
      try {
        const res = await instrumentTypeService().retrieve();
        instrumentTypes.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveInstrumentTypes();
    };

    onMounted(async () => {
      await retrieveInstrumentTypes();
    });

    const removeId: Ref<number> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: IInstrumentType) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeInstrumentType = async () => {
      try {
        await instrumentTypeService().delete(removeId.value);
        const message = t$('aniScanApp.instrumentType.deleted', { param: removeId.value }).toString();
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveInstrumentTypes();
        closeDialog();
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    return {
      instrumentTypes,
      handleSyncList,
      isFetching,
      retrieveInstrumentTypes,
      clear,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeInstrumentType,
      t$,
    };
  },
});
