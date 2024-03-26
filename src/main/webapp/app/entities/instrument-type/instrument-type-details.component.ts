import { defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import InstrumentTypeService from './instrument-type.service';
import { type IInstrumentType } from '@/shared/model/instrument-type.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'InstrumentTypeDetails',
  setup() {
    const instrumentTypeService = inject('instrumentTypeService', () => new InstrumentTypeService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const instrumentType: Ref<IInstrumentType> = ref({});

    const retrieveInstrumentType = async instrumentTypeId => {
      try {
        const res = await instrumentTypeService().find(instrumentTypeId);
        instrumentType.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.instrumentTypeId) {
      retrieveInstrumentType(route.params.instrumentTypeId);
    }

    return {
      alertService,
      instrumentType,

      previousState,
      t$: useI18n().t,
    };
  },
});
