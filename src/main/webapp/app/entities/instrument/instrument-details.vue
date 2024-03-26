<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="instrument">
        <h2 class="jh-entity-heading" data-cy="instrumentDetailsHeading">
          <span v-text="t$('aniScanApp.instrument.detail.title')"></span> {{ instrument.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('aniScanApp.instrument.secCode')"></span>
          </dt>
          <dd>
            <span>{{ instrument.secCode }}</span>
          </dd>
          <dt>
            <span v-text="t$('aniScanApp.instrument.type')"></span>
          </dt>
          <dd>
            <div v-if="instrument.type">
              <router-link :to="{ name: 'InstrumentTypeView', params: { instrumentTypeId: instrument.type.id } }">{{
                instrument.type.id
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link
          v-if="instrument.id"
          :to="{ name: 'InstrumentEdit', params: { instrumentId: instrument.id } }"
          custom
          v-slot="{ navigate }"
        >
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.edit')"></span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./instrument-details.component.ts"></script>
