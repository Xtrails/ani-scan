import { defineComponent, provide } from 'vue';

import InstrumentTypeService from './instrument-type/instrument-type.service';
import InstrumentService from './instrument/instrument.service';
import RobotService from './robot/robot.service';
import UserService from '@/entities/user/user.service';
// jhipster-needle-add-entity-service-to-entities-component-import - JHipster will import entities services here

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'Entities',
  setup() {
    provide('userService', () => new UserService());
    provide('instrumentTypeService', () => new InstrumentTypeService());
    provide('instrumentService', () => new InstrumentService());
    provide('robotService', () => new RobotService());
    // jhipster-needle-add-entity-service-to-entities-component - JHipster will import entities services here
  },
});
