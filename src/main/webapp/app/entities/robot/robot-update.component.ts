import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import RobotService from './robot.service';
import { useValidation, useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import InstrumentService from '@/entities/instrument/instrument.service';
import { type IInstrument } from '@/shared/model/instrument.model';
import { type IRobot, Robot } from '@/shared/model/robot.model';
import { RobotType } from '@/shared/model/enumerations/robot-type.model';
import { OperationType } from '@/shared/model/enumerations/operation-type.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'RobotUpdate',
  setup() {
    const robotService = inject('robotService', () => new RobotService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const robot: Ref<IRobot> = ref(new Robot());

    const instrumentService = inject('instrumentService', () => new InstrumentService());

    const instruments: Ref<IInstrument[]> = ref([]);
    const robotTypeValues: Ref<string[]> = ref(Object.keys(RobotType));
    const operationTypeValues: Ref<string[]> = ref(Object.keys(OperationType));
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveRobot = async robotId => {
      try {
        const res = await robotService().find(robotId);
        res.firstOperationDttm = new Date(res.firstOperationDttm);
        res.lastOperationDttm = new Date(res.lastOperationDttm);
        robot.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.robotId) {
      retrieveRobot(route.params.robotId);
    }

    const initRelationships = () => {
      instrumentService()
        .retrieve()
        .then(res => {
          instruments.value = res.data;
        });
    };

    initRelationships();

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      type: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      lots: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      period: {
        required: validations.required(t$('entity.validation.required').toString()),
        numeric: validations.numeric(t$('entity.validation.number').toString()),
      },
      operationType: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      operationCount: {
        required: validations.required(t$('entity.validation.required').toString()),
        numeric: validations.numeric(t$('entity.validation.number').toString()),
      },
      firstOperationDttm: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      lastOperationDttm: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      lastPrice: {},
      volumeByHour: {},
      instrument: {},
    };
    const v$ = useVuelidate(validationRules, robot as any);
    v$.value.$validate();

    return {
      robotService,
      alertService,
      robot,
      previousState,
      robotTypeValues,
      operationTypeValues,
      isSaving,
      currentLanguage,
      instruments,
      v$,
      ...useDateFormat({ entityRef: robot }),
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.robot.id) {
        this.robotService()
          .update(this.robot)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('aniScanApp.robot.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.robotService()
          .create(this.robot)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('aniScanApp.robot.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
