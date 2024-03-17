<template>
  <div>
    <h2 id="page-heading" data-cy="RobotHeading">
      <span v-text="t$('aniScanApp.robot.home.title')" id="robot-heading"></span>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && robots && robots.length === 0">
      <span v-text="t$('aniScanApp.robot.home.notFound')"></span>
    </div>
    <div class="table-responsive" v-if="robots && robots.length > 0">
      <table class="table table-striped" aria-describedby="robots">
        <thead>
          <tr>
            <th scope="row" v-on:click="changeOrder('instrument.secCode')">
              <span v-text="t$('aniScanApp.robot.instrument')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'instrument.secCode'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('operationType')">
              <span v-text="t$('aniScanApp.robot.operationType')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'operationType'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('lots')">
              <span v-text="t$('aniScanApp.robot.lots')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'lots'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('period')">
              <span v-text="t$('aniScanApp.robot.period')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'period'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('operationCount')">
              <span v-text="t$('aniScanApp.robot.operationCount')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'operationCount'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('volumeByHour')">
              <span v-text="t$('aniScanApp.robot.volumeByHour')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'volumeByHour'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('firstOperationDttm')">
              <span v-text="t$('aniScanApp.robot.firstOperationDttm')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'firstOperationDttm'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('lastOperationDttm')">
              <span v-text="t$('aniScanApp.robot.lastOperationDttm')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'lastOperationDttm'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('nextOperationDttm')">
              <span v-text="t$('aniScanApp.robot.nextOperationDttm')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'nextOperationDttm'"></jhi-sort-indicator>
            </th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="robot in robots" :key="robot.id" data-cy="entityTable">
            <td>{{ robot.instrument.secCode }}</td>
            <td
              class="buy-operation"
              v-text="t$('aniScanApp.OperationType.' + robot.operationType)"
              v-if="robot.operationType == 'BUY'"
            ></td>
            <td
              class="sell-operation"
              v-text="t$('aniScanApp.OperationType.' + robot.operationType)"
              v-if="robot.operationType == 'SELL'"
            ></td>
            <td>{{ robot.lots }}</td>
            <td>{{ robot.period }}</td>
            <td>{{ robot.operationCount }}</td>
            <td>{{ robot.volumeByHour }}</td>
            <td>{{ formatOnlyTimeShort(robot.firstOperationDttm) || '' }}</td>
            <td>{{ formatOnlyTimeShort(robot.lastOperationDttm) || '' }}</td>
            <td>{{ formatOnlyTimeShort(robot.nextOperationDttm) || '' }}</td>
          </tr>
        </tbody>
        <span ref="infiniteScrollEl"></span>
      </table>
    </div>
  </div>
</template>

<script lang="ts" src="./robot.component.ts"></script>
