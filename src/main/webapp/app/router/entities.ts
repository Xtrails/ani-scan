import { Authority } from '@/shared/security/authority';
/* tslint:disable */
// prettier-ignore
const Entities = () => import('@/entities/entities.vue');

const InstrumentType = () => import('@/entities/instrument-type/instrument-type.vue');
const InstrumentTypeUpdate = () => import('@/entities/instrument-type/instrument-type-update.vue');
const InstrumentTypeDetails = () => import('@/entities/instrument-type/instrument-type-details.vue');

const Instrument = () => import('@/entities/instrument/instrument.vue');
const InstrumentUpdate = () => import('@/entities/instrument/instrument-update.vue');
const InstrumentDetails = () => import('@/entities/instrument/instrument-details.vue');

const Robot = () => import('@/entities/robot/robot.vue');
const RobotTable = () => import('@/entities/robot/table/robot.vue');
const RobotUpdate = () => import('@/entities/robot/robot-update.vue');
const RobotDetails = () => import('@/entities/robot/robot-details.vue');

// jhipster-needle-add-entity-to-router-import - JHipster will import entities to the router here

export default {
  path: '/',
  component: Entities,
  children: [
    {
      path: 'instrument-type',
      name: 'InstrumentType',
      component: InstrumentType,
      meta: { authorities: [Authority.ADMIN] },
    },
    {
      path: 'instrument-type/new',
      name: 'InstrumentTypeCreate',
      component: InstrumentTypeUpdate,
      meta: { authorities: [Authority.ADMIN] },
    },
    {
      path: 'instrument-type/:instrumentTypeId/edit',
      name: 'InstrumentTypeEdit',
      component: InstrumentTypeUpdate,
      meta: { authorities: [Authority.ADMIN] },
    },
    {
      path: 'instrument-type/:instrumentTypeId/view',
      name: 'InstrumentTypeView',
      component: InstrumentTypeDetails,
      meta: { authorities: [Authority.ADMIN] },
    },
    {
      path: 'instrument',
      name: 'Instrument',
      component: Instrument,
      meta: { authorities: [Authority.ADMIN] },
    },
    {
      path: 'instrument/new',
      name: 'InstrumentCreate',
      component: InstrumentUpdate,
      meta: { authorities: [Authority.ADMIN] },
    },
    {
      path: 'instrument/:instrumentId/edit',
      name: 'InstrumentEdit',
      component: InstrumentUpdate,
      meta: { authorities: [Authority.ADMIN] },
    },
    {
      path: 'instrument/:instrumentId/view',
      name: 'InstrumentView',
      component: InstrumentDetails,
      meta: { authorities: [Authority.ADMIN] },
    },
    {
      path: 'robot',
      name: 'Robot',
      component: Robot,
      meta: { authorities: [Authority.ADMIN] },
    },
    {
      path: 'robot/new',
      name: 'RobotCreate',
      component: RobotUpdate,
      meta: { authorities: [Authority.ADMIN] },
    },
    {
      path: 'robot/:robotId/edit',
      name: 'RobotEdit',
      component: RobotUpdate,
      meta: { authorities: [Authority.ADMIN] },
    },
    {
      path: 'robot/:robotId/view',
      name: 'RobotView',
      component: RobotDetails,
      meta: { authorities: [Authority.ADMIN] },
    },
    {
      path: 'robots/table',
      name: 'RobotTable',
      component: RobotTable,
      // meta: { authorities: [Authority.USER, Authority.ADMIN] },
    },
    // jhipster-needle-add-entity-to-router - JHipster will add entities to the router here
  ],
};
