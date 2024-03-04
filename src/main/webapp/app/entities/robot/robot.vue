<template>
  <div>
    <h2 id="page-heading" data-cy="RobotHeading">
      <span v-text="t$('aniScanApp.robot.home.title')" id="robot-heading"></span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" v-on:click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="t$('aniScanApp.robot.home.refreshListLabel')"></span>
        </button>
        <router-link :to="{ name: 'RobotCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-robot"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="t$('aniScanApp.robot.home.createLabel')"></span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && robots && robots.length === 0">
      <span v-text="t$('aniScanApp.robot.home.notFound')"></span>
    </div>
    <div class="table-responsive" v-if="robots && robots.length > 0">
      <table class="table table-striped" aria-describedby="robots">
        <thead>
          <tr>
            <th scope="row" v-on:click="changeOrder('id')">
              <span v-text="t$('global.field.id')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('type')">
              <span v-text="t$('aniScanApp.robot.type')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'type'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('lots')">
              <span v-text="t$('aniScanApp.robot.lots')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'lots'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('period')">
              <span v-text="t$('aniScanApp.robot.period')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'period'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('operationType')">
              <span v-text="t$('aniScanApp.robot.operationType')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'operationType'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('operationCount')">
              <span v-text="t$('aniScanApp.robot.operationCount')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'operationCount'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('firstOperationDttm')">
              <span v-text="t$('aniScanApp.robot.firstOperationDttm')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'firstOperationDttm'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('lastOperationDttm')">
              <span v-text="t$('aniScanApp.robot.lastOperationDttm')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'lastOperationDttm'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('lastPrice')">
              <span v-text="t$('aniScanApp.robot.lastPrice')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'lastPrice'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('volumeByHour')">
              <span v-text="t$('aniScanApp.robot.volumeByHour')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'volumeByHour'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('instrument.id')">
              <span v-text="t$('aniScanApp.robot.instrument')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'instrument.id'"></jhi-sort-indicator>
            </th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="robot in robots" :key="robot.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'RobotView', params: { robotId: robot.id } }">{{ robot.id }}</router-link>
            </td>
            <td v-text="t$('aniScanApp.RobotType.' + robot.type)"></td>
            <td>{{ robot.lots }}</td>
            <td>{{ robot.period }}</td>
            <td v-text="t$('aniScanApp.OperationType.' + robot.operationType)"></td>
            <td>{{ robot.operationCount }}</td>
            <td>{{ formatDateShort(robot.firstOperationDttm) || '' }}</td>
            <td>{{ formatDateShort(robot.lastOperationDttm) || '' }}</td>
            <td>{{ robot.lastPrice }}</td>
            <td>{{ robot.volumeByHour }}</td>
            <td>
              <div v-if="robot.instrument">
                <router-link :to="{ name: 'InstrumentView', params: { instrumentId: robot.instrument.id } }">{{
                  robot.instrument.id
                }}</router-link>
              </div>
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'RobotView', params: { robotId: robot.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.view')"></span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'RobotEdit', params: { robotId: robot.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.edit')"></span>
                  </button>
                </router-link>
                <b-button
                  v-on:click="prepareRemove(robot)"
                  variant="danger"
                  class="btn btn-sm"
                  data-cy="entityDeleteButton"
                  v-b-modal.removeEntity
                >
                  <font-awesome-icon icon="times"></font-awesome-icon>
                  <span class="d-none d-md-inline" v-text="t$('entity.action.delete')"></span>
                </b-button>
              </div>
            </td>
          </tr>
        </tbody>
        <span ref="infiniteScrollEl"></span>
      </table>
    </div>
    <b-modal ref="removeEntity" id="removeEntity">
      <template #modal-title>
        <span id="aniScanApp.robot.delete.question" data-cy="robotDeleteDialogHeading" v-text="t$('entity.delete.title')"></span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-robot-heading" v-text="t$('aniScanApp.robot.delete.question', { id: removeId })"></p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-text="t$('entity.action.cancel')" v-on:click="closeDialog()"></button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-robot"
            data-cy="entityConfirmDeleteButton"
            v-text="t$('entity.action.delete')"
            v-on:click="removeRobot()"
          ></button>
        </div>
      </template>
    </b-modal>
  </div>
</template>

<script lang="ts" src="./robot.component.ts"></script>
