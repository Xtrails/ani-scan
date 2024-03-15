import { defineComponent, inject, onMounted, ref, type Ref, watch, watchEffect } from 'vue';
import { useI18n } from 'vue-i18n';
import { useIntersectionObserver } from '@vueuse/core';

import { type IRobot } from '@/shared/model/robot.model';
import useDataUtils from '@/shared/data/data-utils.service';
import { useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';
import SimpleRobotService from './simple-robot.service';
import SockJS from 'sockjs-client';
import Stomp from 'webstomp-client';
import { Robot } from '../../../shared/model/robot.model';
import { RobotType } from '../../../shared/model/enumerations/robot-type.model';
import { Instrument } from '../../../shared/model/instrument.model';
import '@grapecity/wijmo.styles/wijmo.css';
// import "bootstrap.css";
import '@grapecity/wijmo.vue2.grid';
import * as wjcGrid from '@grapecity/wijmo.grid';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'Robot',
  setup() {
    const { t: t$ } = useI18n();
    const dateFormat = useDateFormat();
    const dataUtils = useDataUtils();
    const simpleRobotService = inject('simpleRobotService', () => new SimpleRobotService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const itemsPerPage = ref(50);
    const queryCount: Ref<number> = ref(null);
    const page: Ref<number> = ref(1);
    const propOrder = ref('id');
    const reverse = ref(false);
    const totalItems = ref(0);
    const links: Ref<any> = ref({});
    const robots: Ref<IRobot[]> = ref([]);
    const isFetching = ref(false);
    let lastChangeRobotsDttm;

    const clear = () => {
      page.value = 1;
      links.value = {};
      robots.value = [];
    };

    // const sort = (): Array<any> => {
    //   const result = [propOrder.value + ',' + (reverse.value ? 'desc' : 'asc')];
    //   if (propOrder.value !== 'id') {
    //     result.push('id');
    //   }
    //   return result;
    // };

    function lotsToNumber(lots: string) {
      const index = lots.indexOf('-');
      if (index != null && index > 0) {
        return parseInt(lots.substring(index + 2, lots.length));
      } else {
        return parseInt(lots);
      }
    }

    const sort = () => {
      console.log('propOrder.value = ', propOrder.value);
      switch (propOrder.value) {
        case 'lots':
          if (reverse.value) {
            robots.value.sort((a, b) =>
              lotsToNumber(a.lots) > lotsToNumber(b.lots) ? 1 : lotsToNumber(b.lots) > lotsToNumber(a.lots) ? -1 : 0,
            );
          } else {
            robots.value.sort((a, b) =>
              lotsToNumber(a.lots) > lotsToNumber(b.lots) ? -1 : lotsToNumber(b.lots) > lotsToNumber(a.lots) ? 1 : 0,
            );
          }
          break;
        case 'period':
          if (reverse.value) {
            robots.value.sort((a, b) => (a.period > b.period ? 1 : b.period > a.period ? -1 : 0));
          } else {
            robots.value.sort((a, b) => (a.period > b.period ? -1 : b.period > a.period ? 1 : 0));
          }
          break;
        case 'operationType':
          if (reverse.value) {
            robots.value.sort((a, b) => (a.operationType > b.operationType ? 1 : b.operationType > a.operationType ? -1 : 0));
          } else {
            robots.value.sort((a, b) => (a.operationType > b.operationType ? -1 : b.operationType > a.operationType ? 1 : 0));
          }
          break;
        case 'operationCount':
          if (reverse.value) {
            robots.value.sort((a, b) => (a.operationCount > b.operationCount ? 1 : b.operationCount > a.operationCount ? -1 : 0));
          } else {
            robots.value.sort((a, b) => (a.operationCount > b.operationCount ? -1 : b.operationCount > a.operationCount ? 1 : 0));
          }
          break;
        case 'firstOperationDttm':
          if (reverse.value) {
            robots.value.sort((a, b) =>
              a.firstOperationDttm > b.firstOperationDttm ? 1 : b.firstOperationDttm > a.firstOperationDttm ? -1 : 0,
            );
          } else {
            robots.value.sort((a, b) =>
              a.firstOperationDttm > b.firstOperationDttm ? -1 : b.firstOperationDttm > a.firstOperationDttm ? 1 : 0,
            );
          }
          break;
        case 'lastOperationDttm':
          if (reverse.value) {
            robots.value.sort((a, b) =>
              a.lastOperationDttm > b.lastOperationDttm ? 1 : b.lastOperationDttm > a.lastOperationDttm ? -1 : 0,
            );
          } else {
            robots.value.sort((a, b) =>
              a.lastOperationDttm > b.lastOperationDttm ? -1 : b.lastOperationDttm > a.lastOperationDttm ? 1 : 0,
            );
          }
          break;
        case 'lastPrice':
          if (reverse.value) {
            robots.value.sort((a, b) => (a.lastPrice > b.lastPrice ? 1 : b.lastPrice > a.lastPrice ? -1 : 0));
          } else {
            robots.value.sort((a, b) => (a.lastPrice > b.lastPrice ? -1 : b.lastPrice > a.lastPrice ? 1 : 0));
          }
          break;
        case 'volumeByHour':
          if (reverse.value) {
            robots.value.sort((a, b) => (a.volumeByHour > b.volumeByHour ? 1 : b.volumeByHour > a.volumeByHour ? -1 : 0));
          } else {
            robots.value.sort((a, b) => (a.volumeByHour > b.volumeByHour ? -1 : b.volumeByHour > a.volumeByHour ? 1 : 0));
          }
          break;
        case 'instrument.secCode':
          if (reverse.value) {
            robots.value.sort((a, b) =>
              a.instrument.secCode > b.instrument.secCode ? 1 : b.instrument.secCode > a.instrument.secCode ? -1 : 0,
            );
          } else {
            robots.value.sort((a, b) =>
              a.instrument.secCode > b.instrument.secCode ? -1 : b.instrument.secCode > a.instrument.secCode ? 1 : 0,
            );
          }
          break;
      }
    };

    function insertRobot(data: any) {
      let robot = new Robot(
        data.id,
        RobotType.SIMPLE,
        data.lot,
        data.pr,
        data.op_t,
        data.op_c,
        data.f_op_dttm,
        data.l_op_dttm,
        data.n_op_dttm,
        data.l_pr,
        data.vol,
        new Instrument(0, data.code),
      );
      robots.value.push(robot);
      sort();
    }

    function deleteRobot(data: any) {
      if (robots.value.some(e => e.id === data.id)) {
        const index = robots.value.findIndex(e => e.id === data.id);
        robots.value.splice(index, 1);
        sort();
      }
    }

    function updateRobot(data: any) {
      if (robots.value.some(e => e.id === data.id)) {
        const index = robots.value.findIndex(e => e.id === data.id);
        if (data.lot != null) {
          robots.value[index].lots = data.lot;
        }
        if (data.pr != null) {
          robots.value[index].period = data.pr;
        }
        if (data.op_c != null) {
          robots.value[index].operationCount = data.op_c;
        }
        if (data.f_op_dttm != null) {
          robots.value[index].firstOperationDttm = data.f_op_dttm;
        }
        if (data.l_op_dttm != null) {
          robots.value[index].lastOperationDttm = data.l_op_dttm;
        }
        if (data.n_op_dttm != null) {
          robots.value[index].nextOperationDttm = data.n_op_dttm;
        }
        if (data.l_pr != null) {
          robots.value[index].lastPrice = data.l_pr;
        }
        if (data.vol != null) {
          robots.value[index].volumeByHour = data.vol;
        }
        sort();
      }
    }

    const retrieveRobots = async () => {
      const socket = new SockJS('http://localhost:8081/ws');
      const stompClient = Stomp.over(socket);
      let connected = false;
      stompClient.connect(
        {},
        frame => {
          connected = true;
          // console.log(frame);
          stompClient.subscribe('/app/robots/all', msg => {
            // console.log(msg);
            let content = JSON.parse(msg.body);
            lastChangeRobotsDttm = content.last_ch_dttm; //todo: переводить во время
            content.simple_r.forEach(event => {
              let eventType = event.type;
              let data = event.data;
              switch (eventType) {
                case 'INSERT':
                  insertRobot(data);
                  break;
                case 'DELETE':
                  deleteRobot(data);
                  break;
                case 'UPDATE':
                  updateRobot(data);
                  break;
              }
            });
          });
        },
        error => {
          console.log(error);
          connected = false;
        },
      );
      isFetching.value = true;
      try {
        // const paginationQuery = {
        //   page: page.value - 1,
        //   size: itemsPerPage.value,
        //   sort: sort(),
        // };
        // console.log(paginationQuery)
        // const res = await simpleRobotService().retrieve(paginationQuery);
        // totalItems.value = Number(res.headers['x-total-count']);
        // queryCount.value = totalItems.value;
        // links.value = dataUtils.parseLinks(res.headers?.['link']);
        // for (let i = 0; i < res.data.length; i++) {
        //     if (robots.value.some(e => e.id === res.data[i].id)) {
        //       const index = robots.value.findIndex(e => e.id === res.data[i].id)
        //       // console.log("update from", res.data[i]);
        //       // console.log("update to", robots.value[index]);
        //       robots.value[index] = res.data[i];
        //     } else {
        //       // console.log("push", res.data[i]);
        //       robots.value.push(res.data[i]);
        //     }
        // }
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      clear();
    };

    onMounted(async () => {
      // while (true) {
      await retrieveRobots();
      //   await delay(retryDelay.value);
      // }
    });

    const removeId: Ref<number> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: IRobot) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeRobot = async () => {
      try {
        await simpleRobotService().delete(removeId.value);
        const message = t$('aniScanApp.robot.deleted', { param: removeId.value }).toString();
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        clear();
        closeDialog();
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    const changeOrder = (newOrder: string) => {
      if (propOrder.value === newOrder) {
        reverse.value = !reverse.value;
      } else {
        reverse.value = false;
      }
      propOrder.value = newOrder;
    };

    // Whenever order changes, reset the pagination
    watch([propOrder, reverse], () => {
      clear();
    });

    // Whenever the data resets or page changes, switch to the new page.
    watch([robots, page], async ([data, page], [_prevData, prevPage]) => {
      if (data.length === 0 || page !== prevPage) {
        await retrieveRobots();
      }
    });

    const infiniteScrollEl = ref<HTMLElement>(null);
    const intersectionObserver = useIntersectionObserver(
      infiniteScrollEl,
      intersection => {
        if (intersection[0].isIntersecting && !isFetching.value) {
          page.value++;
        }
      },
      {
        threshold: 0.5,
        immediate: false,
      },
    );
    watchEffect(() => {
      if (links.value.next) {
        intersectionObserver.resume();
      } else if (intersectionObserver.isActive) {
        intersectionObserver.pause();
      }
    });

    return {
      robots,
      handleSyncList,
      isFetching,
      retrieveRobots,
      clear,
      ...dateFormat,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeRobot,
      itemsPerPage,
      queryCount,
      page,
      propOrder,
      reverse,
      totalItems,
      changeOrder,
      infiniteScrollEl,
      t$,
      ...dataUtils,
    };
  },
});
