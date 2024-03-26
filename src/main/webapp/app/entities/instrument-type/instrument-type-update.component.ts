import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import InstrumentTypeService from './instrument-type.service';
import { useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import { type IInstrumentType, InstrumentType } from '@/shared/model/instrument-type.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'InstrumentTypeUpdate',
  setup() {
    const instrumentTypeService = inject('instrumentTypeService', () => new InstrumentTypeService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const instrumentType: Ref<IInstrumentType> = ref(new InstrumentType());
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

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

    const initRelationships = () => {};

    initRelationships();

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      name: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      instruments: {},
    };
    const v$ = useVuelidate(validationRules, instrumentType as any);
    v$.value.$validate();

    return {
      instrumentTypeService,
      alertService,
      instrumentType,
      previousState,
      isSaving,
      currentLanguage,
      v$,
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.instrumentType.id) {
        this.instrumentTypeService()
          .update(this.instrumentType)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('aniScanApp.instrumentType.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.instrumentTypeService()
          .create(this.instrumentType)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('aniScanApp.instrumentType.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
