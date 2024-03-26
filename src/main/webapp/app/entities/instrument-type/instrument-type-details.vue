<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="instrumentType">
        <h2 class="jh-entity-heading" data-cy="instrumentTypeDetailsHeading">
          <span v-text="t$('aniScanApp.instrumentType.detail.title')"></span> {{ instrumentType.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('aniScanApp.instrumentType.name')"></span>
          </dt>
          <dd>
            <span>{{ instrumentType.name }}</span>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link
          v-if="instrumentType.id"
          :to="{ name: 'InstrumentTypeEdit', params: { instrumentTypeId: instrumentType.id } }"
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

<script lang="ts" src="./instrument-type-details.component.ts"></script>
