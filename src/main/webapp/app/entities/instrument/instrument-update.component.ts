import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import InstrumentService from './instrument.service';
import { useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import InstrumentTypeService from '@/entities/instrument-type/instrument-type.service';
import { type IInstrumentType } from '@/shared/model/instrument-type.model';
import { type IInstrument, Instrument } from '@/shared/model/instrument.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'InstrumentUpdate',
  setup() {
    const instrumentService = inject('instrumentService', () => new InstrumentService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const instrument: Ref<IInstrument> = ref(new Instrument());

    const instrumentTypeService = inject('instrumentTypeService', () => new InstrumentTypeService());

    const instrumentTypes: Ref<IInstrumentType[]> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

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

    const initRelationships = () => {
      instrumentTypeService()
        .retrieve()
        .then(res => {
          instrumentTypes.value = res.data;
        });
    };

    initRelationships();

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      secCode: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      robots: {},
      type: {},
    };
    const v$ = useVuelidate(validationRules, instrument as any);
    v$.value.$validate();

    return {
      instrumentService,
      alertService,
      instrument,
      previousState,
      isSaving,
      currentLanguage,
      instrumentTypes,
      v$,
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.instrument.id) {
        this.instrumentService()
          .update(this.instrument)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('aniScanApp.instrument.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.instrumentService()
          .create(this.instrument)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('aniScanApp.instrument.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
