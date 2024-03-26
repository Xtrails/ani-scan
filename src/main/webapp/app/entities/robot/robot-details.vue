<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="robot">
        <h2 class="jh-entity-heading" data-cy="robotDetailsHeading">
          <span v-text="t$('aniScanApp.robot.detail.title')"></span> {{ robot.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('aniScanApp.robot.type')"></span>
          </dt>
          <dd>
            <span v-text="t$('aniScanApp.RobotType.' + robot.type)"></span>
          </dd>
          <dt>
            <span v-text="t$('aniScanApp.robot.lots')"></span>
          </dt>
          <dd>
            <span>{{ robot.lots }}</span>
          </dd>
          <dt>
            <span v-text="t$('aniScanApp.robot.period')"></span>
          </dt>
          <dd>
            <span>{{ robot.period }}</span>
          </dd>
          <dt>
            <span v-text="t$('aniScanApp.robot.operationType')"></span>
          </dt>
          <dd>
            <span v-text="t$('aniScanApp.OperationType.' + robot.operationType)"></span>
          </dd>
          <dt>
            <span v-text="t$('aniScanApp.robot.operationCount')"></span>
          </dt>
          <dd>
            <span>{{ robot.operationCount }}</span>
          </dd>
          <dt>
            <span v-text="t$('aniScanApp.robot.firstOperationDttm')"></span>
          </dt>
          <dd>
            <span v-if="robot.firstOperationDttm">{{ formatDateLong(robot.firstOperationDttm) }}</span>
          </dd>
          <dt>
            <span v-text="t$('aniScanApp.robot.lastOperationDttm')"></span>
          </dt>
          <dd>
            <span v-if="robot.lastOperationDttm">{{ formatDateLong(robot.lastOperationDttm) }}</span>
          </dd>
          <dt>
            <span v-text="t$('aniScanApp.robot.detectionDttm')"></span>
          </dt>
          <dd>
            <span v-if="robot.detectionDttm">{{ formatDateLong(robot.detectionDttm) }}</span>
          </dd>
          <dt>
            <span v-text="t$('aniScanApp.robot.lastPrice')"></span>
          </dt>
          <dd>
            <span>{{ robot.lastPrice }}</span>
          </dd>
          <dt>
            <span v-text="t$('aniScanApp.robot.volumeByHour')"></span>
          </dt>
          <dd>
            <span>{{ robot.volumeByHour }}</span>
          </dd>
          <dt>
            <span v-text="t$('aniScanApp.robot.instrument')"></span>
          </dt>
          <dd>
            <div v-if="robot.instrument">
              <router-link :to="{ name: 'InstrumentView', params: { instrumentId: robot.instrument.id } }">{{
                robot.instrument.id
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link v-if="robot.id" :to="{ name: 'RobotEdit', params: { robotId: robot.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.edit')"></span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./robot-details.component.ts"></script>
