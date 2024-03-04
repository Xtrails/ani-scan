import { defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import InstrumentService from './instrument.service';
import { type IInstrument } from '@/shared/model/instrument.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'InstrumentDetails',
  setup() {
    const instrumentService = inject('instrumentService', () => new InstrumentService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const instrument: Ref<IInstrument> = ref({});

    const retrieveInstrument = async instrumentId => {
      try {
        const res = await instrumentService().find(instrumentId);
        instrument.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.instrumentId) {
      retrieveInstrument(route.params.instrumentId);
    }

    return {
      alertService,
      instrument,

      previousState,
      t$: useI18n().t,
    };
  },
});
