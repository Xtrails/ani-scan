import { onBeforeMount, ref } from 'vue';
import 'ag-grid-community/styles/ag-grid.css'; // Mandatory CSS required by the grid
import 'ag-grid-community/styles/ag-theme-quartz.css'; // Optional Theme applied to the grid
import { AgGridVue } from 'ag-grid-vue3';
import { Robot } from '@/shared/model/robot.model';
import { RobotType } from '@/shared/model/enumerations/robot-type.model';
import { Instrument } from '@/shared/model/instrument.model'; // AG Grid Component
import SockJS from 'sockjs-client';
import Stomp from 'webstomp-client';
import { useI18n } from 'vue-i18n';
import moment from 'moment';

export { useDateFormat } from '@/shared/composables/date-format';

export default {
  name: 'App',
  components: {
    AgGridVue, // Add AG Grid Vue3 component
  },
  setup() {
    let lastChangeRobotsDttm;
    const gridApi = ref(null);
    // Row Data: The data to be displayed.
    const rowData = [];
    const propOrder = ref('id');
    const reverse = ref(false);
    const isFetching = ref(false);
    const { t: t$ } = useI18n();
    const autoSizeStrategy = ref(null);
    const TIME_SHORT_FORMAT = 'HH:mm:ss';

    const DEBUG_WEBSOCKET_FLG = true;
    const RECONNECT_WEBSOCKET_DELAY_MS = 2000;
    const WEBSOCKET_CONNECT_ADDRESS = 'http://192.168.0.160:8081/ws';
    const WEBSOCKET_SUBSCRIBE_ADDRESS = '/app/robots/all';

    const rowHeight = 23;
    const width = '100%';
    const height = '100%';
    const themeClass = 'ag-theme-quartz';
    const rowSelection = 'multiple';
    const flashDuration = 60000;
    const fadeDuration = 15000;
    // const themeClass = "ag-theme-quartz-dark";

    //Headers column params
    const defaultColDef = ref({
      wrapHeaderText: true,
      autoHeaderHeight: true,
    });

    const getRowId = params => params.data.id;

    // Column Definitions: Defines the columns to be displayed.
    const colDefs = ref([
      {
        colId: 'instrument',
        field: 'instrument',
        width: 95,
        wrapText: true,
        autoHeight: true,
        headerComponentParams: { displayName: t$('aniScanApp.robot.instrument').toString() },
        valueFormatter: instrumentFormatter,
        cellStyle: { 'text-align': 'center', 'font-weight': '650', 'padding-left': '4px', 'padding-right': '2px', 'padding-bottom': '1px' },
        // headerCheckboxSelection: true,
        checkboxSelection: true,
        showDisabledCheckboxes: true,
        // filter: true,
      },
      {
        colId: 'operationType',
        field: 'operationType',
        width: 85,
        wrapText: true,
        autoHeight: true,
        headerComponentParams: { displayName: t$('aniScanApp.robot.operationType').toString() },
        cellStyle: params => {
          if (params.value === 'BUY') {
            return { color: 'green', 'text-align': 'center', 'font-weight': '500', 'padding-left': '2px', 'padding-right': '2px' };
          }
          if (params.value === 'SELL') {
            return { color: 'red', 'text-align': 'center', 'font-weight': '500', 'padding-left': '2px', 'padding-right': '2px' };
          }
          return null;
        },
        // filter: true,
      },
      {
        colId: 'lots',
        field: 'lots',
        width: 70,
        wrapText: true,
        autoHeight: true,
        headerComponentParams: { displayName: t$('aniScanApp.robot.lots').toString() },
        cellStyle: { 'text-align': 'center', 'font-weight': '500', 'padding-left': '2px', 'padding-right': '2px' },
        comparator: (valueA, valueB, nodeA, nodeB, isDescending) => {
          if (valueA == valueB) {
            return 0;
          }
          return lotsToNumber(valueA) < lotsToNumber(valueB) ? -1 : lotsToNumber(valueB) < lotsToNumber(valueA) ? 1 : 0;
        },
        // filter: true,
      },
      {
        colId: 'period',
        field: 'period',
        width: 70,
        wrapText: true,
        autoHeight: true,
        headerComponentParams: { displayName: t$('aniScanApp.robot.period').toString() },
        cellStyle: { 'text-align': 'right', 'font-weight': '500', 'padding-left': '2px', 'padding-right': '2px' },
        // filter: true,
      },
      {
        colId: 'operationCount',
        field: 'operationCount',
        width: 70,
        wrapText: true,
        autoHeight: true,
        headerComponentParams: { displayName: t$('aniScanApp.robot.operationCount').toString() },
        valueGetter: 'data.operationCount',
        cellStyle: { 'text-align': 'right', 'padding-left': '2px', 'padding-right': '2px' },
        // filter: true,
      },
      {
        colId: 'volumeByHour',
        field: 'volumeByHour',
        width: 70,
        wrapText: true,
        autoHeight: true,
        headerComponentParams: { displayName: t$('aniScanApp.robot.volumeByHour').toString() },
        valueFormatter: volumeByHourFormatter,
        cellStyle: { 'text-align': 'right', 'padding-left': '2px', 'padding-right': '2px' },
        // filter: true,
      },
      {
        colId: 'detectionDttm',
        field: 'detectionDttm',
        width: 107,
        wrapText: true,
        autoHeight: true,
        headerComponentParams: { displayName: t$('aniScanApp.robot.detectionDttm').toString() },
        valueFormatter: onlyTimeFormatter,
        cellStyle: { 'text-align': 'right', 'padding-left': '2px', 'padding-right': '2px' },
      },
      {
        colId: 'firstOperationDttm',
        field: 'firstOperationDttm',
        width: 100,
        wrapText: true,
        autoHeight: true,
        headerComponentParams: { displayName: t$('aniScanApp.robot.firstOperationDttm').toString() },
        valueFormatter: onlyTimeFormatter,
        cellStyle: { 'text-align': 'right', 'padding-left': '2px', 'padding-right': '2px' },
      },
      {
        colId: 'lastOperationDttm',
        field: 'lastOperationDttm',
        width: 100,
        wrapText: true,
        autoHeight: true,
        headerComponentParams: { displayName: t$('aniScanApp.robot.lastOperationDttm').toString() },
        valueFormatter: onlyTimeFormatter,
        cellStyle: { 'text-align': 'right', 'padding-left': '2px', 'padding-right': '2px' },
      },
      {
        colId: 'nextOperationDttm',
        field: 'nextOperationDttm',
        width: 100,
        wrapText: true,
        autoHeight: true,
        headerComponentParams: { displayName: t$('aniScanApp.robot.nextOperationDttm').toString() },
        valueFormatter: onlyTimeFormatter,
        cellStyle: { 'text-align': 'right', 'padding-left': '2px', 'padding-right': '2px' },
      },
      // {colId: "lastPrice", field: "lastPrice", headerComponentParams: { displayName: t$('aniScanApp.robot.lastPrice').toString() }},
      // {colId: "type", field: "type", headerComponentParams: { displayName: t$('aniScanApp.robot.type').toString() }},
      // {field: "id", headerComponentParams: {displayName: t$('aniScanApp.robot.id').toString()}},
    ]);

    function lotsToNumber(lots: string) {
      const index = lots.indexOf('-');
      if (index != null && index > 0) {
        return parseInt(lots.substring(index + 2, lots.length));
      } else {
        return parseInt(lots);
      }
    }

    function onlyTimeFormatter(params): string {
      if (params.value) {
        return moment(String(params.value)).format(TIME_SHORT_FORMAT);
      }
      return '';
    }

    function instrumentFormatter(params): string {
      if (params.value) {
        return params.value.secCode;
      }
      return '';
    }

    function volumeByHourFormatter(params) {
      return params.value.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ' ');
    }

    onBeforeMount(() => {
      autoSizeStrategy.value = {
        type: 'fitGridWidth',
        defaultMinWidth: 100,
        columnLimits: [
          {
            colId: 'lots',
            minWidth: 150,
          },
        ],
      };
    });

    let savedSort: any;

    function restoreFromSave() {
      gridApi.value.applyColumnState({
        state: savedSort,
        defaultState: { sort: null },
      });
    }

    function saveSort() {
      const colState = gridApi.value.getColumnState();
      savedSort = colState
        .filter(function (s) {
          return s.sort != null;
        })
        .map(function (s) {
          return { colId: s.colId, sort: s.sort, sortIndex: s.sortIndex };
        });
    }

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
        data.d_op_dttm,
        data.l_pr,
        data.vol,
        new Instrument(0, data.code),
      );
      rowData.push(robot);
      saveSort();
      gridApi.value.setGridOption('rowData', rowData);
      gridApi.value.refreshCells();
      const rowNode = gridApi.value.getRowNode(data.id);
      gridApi.value.flashCells({ rowNodes: [rowNode], flashDuration: flashDuration, fadeDuration: fadeDuration });
      restoreFromSave();
    }

    function deleteRobot(data: any) {
      if (rowData.some(e => e.id === data.id)) {
        const index = rowData.findIndex(e => e.id === data.id);
        rowData.splice(index, 1);
        saveSort();
        gridApi.value.setGridOption('rowData', rowData);
        gridApi.value.refreshCells();
        restoreFromSave();
      }
    }

    function deleteAllRobots() {
      rowData.splice(0, rowData.length);
      saveSort();
      gridApi.value.setGridOption('rowData', rowData);
      gridApi.value.refreshCells();
      restoreFromSave();
    }

    function updateRobot(data: any) {
      if (rowData.some(e => e.id === data.id)) {
        const index = rowData.findIndex(e => e.id === data.id);
        if (data.lot != null) {
          rowData[index].lots = data.lot;
        }
        if (data.pr != null) {
          rowData[index].period = data.pr;
        }
        if (data.op_c != null) {
          rowData[index].operationCount = data.op_c;
        }
        if (data.d_op_dttm != null) {
          rowData[index].detectionDttm = data.d_op_dttm;
        }
        if (data.f_op_dttm != null) {
          rowData[index].firstOperationDttm = data.f_op_dttm;
        }
        if (data.l_op_dttm != null) {
          rowData[index].lastOperationDttm = data.l_op_dttm;
        }
        if (data.n_op_dttm != null) {
          rowData[index].nextOperationDttm = data.n_op_dttm;
        }
        if (data.l_pr != null) {
          rowData[index].lastPrice = data.l_pr;
        }
        if (data.vol != null) {
          rowData[index].volumeByHour = data.vol;
        }
        // saveSort();
        gridApi.value.setGridOption('rowData', rowData);
        gridApi.value.refreshCells();
        // restoreFromSave();
      }
    }

    onBeforeMount(() => {});

    const onGridReady = params => {
      gridApi.value = params.api;

      let connected = false;
      const reconnectWebSocket = async () => {
        while (!connected) {
          const socket = new SockJS(WEBSOCKET_CONNECT_ADDRESS);
          //, {headers : {'Content-Security-Policy': "default-src 'self'; connect-src 'self'; frame-src 'self' data:; script-src 'self' 'unsafe-inline' 'unsafe-eval' https://storage.googleapis.com; style-src 'self' https://fonts.googleapis.com 'unsafe-inline'; img-src 'self' data:; font-src 'self' https://fonts.gstatic.com data:"}});
          const stompClient = Stomp.over(socket);

          //Выключение логирования сокета
          if (!DEBUG_WEBSOCKET_FLG) {
            stompClient.debug = () => {};
          }
          stompClient.connect(
            {},
            frame => {
              connected = true;
              stompClient.subscribe(WEBSOCKET_SUBSCRIBE_ADDRESS, msg => {
                let content = JSON.parse(msg.body);
                lastChangeRobotsDttm = content.last_ch_dttm; //todo: переводить во время
                if (content.simple_r.length === 0) {
                  deleteAllRobots();
                } else {
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
                }
              });
            },
            error => {
              stompClient.unsubscribe();
              console.log(error);
              connected = false;
              deleteAllRobots();
            },
          );
          isFetching.value = true;
          await new Promise(f => setTimeout(f, RECONNECT_WEBSOCKET_DELAY_MS));
        }
      };
      reconnectWebSocket()
        .then(() => {})
        .catch(error => console.log(error));
    };

    return {
      rowData,
      gridApi,
      onGridReady,
      getRowId,
      colDefs,
      onBeforeMount,
      autoSizeStrategy,
      rowHeight,
      defaultColDef,
      width,
      height,
      themeClass,
      rowSelection,
    };
  },
};
