import { defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import RobotService from './robot.service';
import { useDateFormat } from '@/shared/composables';
import { type IRobot } from '@/shared/model/robot.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'RobotDetails',
  setup() {
    const dateFormat = useDateFormat();
    const robotService = inject('robotService', () => new RobotService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const robot: Ref<IRobot> = ref({});

    const retrieveRobot = async robotId => {
      try {
        const res = await robotService().find(robotId);
        robot.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.robotId) {
      retrieveRobot(route.params.robotId);
    }

    return {
      ...dateFormat,
      alertService,
      robot,

      previousState,
      t$: useI18n().t,
    };
  },
});
