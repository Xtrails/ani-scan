<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" role="form" novalidate v-on:submit.prevent="save()">
        <h2
          id="aniScanApp.robot.home.createOrEditLabel"
          data-cy="RobotCreateUpdateHeading"
          v-text="t$('aniScanApp.robot.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="robot.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="robot.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('aniScanApp.robot.type')" for="robot-type"></label>
            <select
              class="form-control"
              name="type"
              :class="{ valid: !v$.type.$invalid, invalid: v$.type.$invalid }"
              v-model="v$.type.$model"
              id="robot-type"
              data-cy="type"
              required
            >
              <option
                v-for="robotType in robotTypeValues"
                :key="robotType"
                v-bind:value="robotType"
                v-bind:label="t$('aniScanApp.RobotType.' + robotType)"
              >
                {{ robotType }}
              </option>
            </select>
            <div v-if="v$.type.$anyDirty && v$.type.$invalid">
              <small class="form-text text-danger" v-for="error of v$.type.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('aniScanApp.robot.lots')" for="robot-lots"></label>
            <input
              type="text"
              class="form-control"
              name="lots"
              id="robot-lots"
              data-cy="lots"
              :class="{ valid: !v$.lots.$invalid, invalid: v$.lots.$invalid }"
              v-model="v$.lots.$model"
              required
            />
            <div v-if="v$.lots.$anyDirty && v$.lots.$invalid">
              <small class="form-text text-danger" v-for="error of v$.lots.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('aniScanApp.robot.period')" for="robot-period"></label>
            <input
              type="number"
              class="form-control"
              name="period"
              id="robot-period"
              data-cy="period"
              :class="{ valid: !v$.period.$invalid, invalid: v$.period.$invalid }"
              v-model.number="v$.period.$model"
              required
            />
            <div v-if="v$.period.$anyDirty && v$.period.$invalid">
              <small class="form-text text-danger" v-for="error of v$.period.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('aniScanApp.robot.operationType')" for="robot-operationType"></label>
            <select
              class="form-control"
              name="operationType"
              :class="{ valid: !v$.operationType.$invalid, invalid: v$.operationType.$invalid }"
              v-model="v$.operationType.$model"
              id="robot-operationType"
              data-cy="operationType"
              required
            >
              <option
                v-for="operationType in operationTypeValues"
                :key="operationType"
                v-bind:value="operationType"
                v-bind:label="t$('aniScanApp.OperationType.' + operationType)"
              >
                {{ operationType }}
              </option>
            </select>
            <div v-if="v$.operationType.$anyDirty && v$.operationType.$invalid">
              <small class="form-text text-danger" v-for="error of v$.operationType.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('aniScanApp.robot.operationCount')" for="robot-operationCount"></label>
            <input
              type="number"
              class="form-control"
              name="operationCount"
              id="robot-operationCount"
              data-cy="operationCount"
              :class="{ valid: !v$.operationCount.$invalid, invalid: v$.operationCount.$invalid }"
              v-model.number="v$.operationCount.$model"
              required
            />
            <div v-if="v$.operationCount.$anyDirty && v$.operationCount.$invalid">
              <small class="form-text text-danger" v-for="error of v$.operationCount.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('aniScanApp.robot.firstOperationDttm')" for="robot-firstOperationDttm"></label>
            <div class="d-flex">
              <input
                id="robot-firstOperationDttm"
                data-cy="firstOperationDttm"
                type="datetime-local"
                class="form-control"
                name="firstOperationDttm"
                :class="{ valid: !v$.firstOperationDttm.$invalid, invalid: v$.firstOperationDttm.$invalid }"
                required
                :value="convertDateTimeFromServer(v$.firstOperationDttm.$model)"
                @change="updateInstantField('firstOperationDttm', $event)"
              />
            </div>
            <div v-if="v$.firstOperationDttm.$anyDirty && v$.firstOperationDttm.$invalid">
              <small class="form-text text-danger" v-for="error of v$.firstOperationDttm.$errors" :key="error.$uid">{{
                error.$message
              }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('aniScanApp.robot.lastOperationDttm')" for="robot-lastOperationDttm"></label>
            <div class="d-flex">
              <input
                id="robot-lastOperationDttm"
                data-cy="lastOperationDttm"
                type="datetime-local"
                class="form-control"
                name="lastOperationDttm"
                :class="{ valid: !v$.lastOperationDttm.$invalid, invalid: v$.lastOperationDttm.$invalid }"
                required
                :value="convertDateTimeFromServer(v$.lastOperationDttm.$model)"
                @change="updateInstantField('lastOperationDttm', $event)"
              />
            </div>
            <div v-if="v$.lastOperationDttm.$anyDirty && v$.lastOperationDttm.$invalid">
              <small class="form-text text-danger" v-for="error of v$.lastOperationDttm.$errors" :key="error.$uid">{{
                error.$message
              }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('aniScanApp.robot.detectionDttm')" for="robot-detectionDttm"></label>
            <div class="d-flex">
              <input
                id="robot-detectionDttm"
                data-cy="detectionDttm"
                type="datetime-local"
                class="form-control"
                name="detectionDttm"
                :class="{ valid: !v$.detectionDttm.$invalid, invalid: v$.detectionDttm.$invalid }"
                required
                :value="convertDateTimeFromServer(v$.detectionDttm.$model)"
                @change="updateInstantField('detectionDttm', $event)"
              />
            </div>
            <div v-if="v$.detectionDttm.$anyDirty && v$.detectionDttm.$invalid">
              <small class="form-text text-danger" v-for="error of v$.detectionDttm.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('aniScanApp.robot.lastPrice')" for="robot-lastPrice"></label>
            <input
              type="number"
              class="form-control"
              name="lastPrice"
              id="robot-lastPrice"
              data-cy="lastPrice"
              :class="{ valid: !v$.lastPrice.$invalid, invalid: v$.lastPrice.$invalid }"
              v-model.number="v$.lastPrice.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('aniScanApp.robot.volumeByHour')" for="robot-volumeByHour"></label>
            <input
              type="number"
              class="form-control"
              name="volumeByHour"
              id="robot-volumeByHour"
              data-cy="volumeByHour"
              :class="{ valid: !v$.volumeByHour.$invalid, invalid: v$.volumeByHour.$invalid }"
              v-model.number="v$.volumeByHour.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('aniScanApp.robot.instrument')" for="robot-instrument"></label>
            <select class="form-control" id="robot-instrument" data-cy="instrument" name="instrument" v-model="robot.instrument">
              <option v-bind:value="null"></option>
              <option
                v-bind:value="robot.instrument && instrumentOption.id === robot.instrument.id ? robot.instrument : instrumentOption"
                v-for="instrumentOption in instruments"
                :key="instrumentOption.id"
              >
                {{ instrumentOption.id }}
              </option>
            </select>
          </div>
        </div>
        <div>
          <button type="button" id="cancel-save" data-cy="entityCreateCancelButton" class="btn btn-secondary" v-on:click="previousState()">
            <font-awesome-icon icon="ban"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.cancel')"></span>
          </button>
          <button
            type="submit"
            id="save-entity"
            data-cy="entityCreateSaveButton"
            :disabled="v$.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.save')"></span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./robot-update.component.ts"></script>
