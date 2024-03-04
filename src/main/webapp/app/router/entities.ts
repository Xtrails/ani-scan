import { Authority } from '@/shared/security/authority';
/* tslint:disable */
// prettier-ignore
const Entities = () => import('@/entities/entities.vue');

const Instrument = () => import('@/entities/instrument/instrument.vue');
const InstrumentUpdate = () => import('@/entities/instrument/instrument-update.vue');
const InstrumentDetails = () => import('@/entities/instrument/instrument-details.vue');

const Robot = () => import('@/entities/robot/robot.vue');
const RobotUpdate = () => import('@/entities/robot/robot-update.vue');
const RobotDetails = () => import('@/entities/robot/robot-details.vue');

// jhipster-needle-add-entity-to-router-import - JHipster will import entities to the router here

export default {
  path: '/',
  component: Entities,
  children: [
    {
      path: 'instrument',
      name: 'Instrument',
      component: Instrument,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'instrument/new',
      name: 'InstrumentCreate',
      component: InstrumentUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'instrument/:instrumentId/edit',
      name: 'InstrumentEdit',
      component: InstrumentUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'instrument/:instrumentId/view',
      name: 'InstrumentView',
      component: InstrumentDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'robot',
      name: 'Robot',
      component: Robot,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'robot/new',
      name: 'RobotCreate',
      component: RobotUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'robot/:robotId/edit',
      name: 'RobotEdit',
      component: RobotUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'robot/:robotId/view',
      name: 'RobotView',
      component: RobotDetails,
      meta: { authorities: [Authority.USER] },
    },
    // jhipster-needle-add-entity-to-router - JHipster will add entities to the router here
  ],
};
