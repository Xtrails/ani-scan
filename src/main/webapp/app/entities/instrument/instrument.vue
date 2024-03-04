<template>
  <div>
    <h2 id="page-heading" data-cy="InstrumentHeading">
      <span v-text="t$('aniScanApp.instrument.home.title')" id="instrument-heading"></span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" v-on:click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="t$('aniScanApp.instrument.home.refreshListLabel')"></span>
        </button>
        <router-link :to="{ name: 'InstrumentCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-instrument"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="t$('aniScanApp.instrument.home.createLabel')"></span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && instruments && instruments.length === 0">
      <span v-text="t$('aniScanApp.instrument.home.notFound')"></span>
    </div>
    <div class="table-responsive" v-if="instruments && instruments.length > 0">
      <table class="table table-striped" aria-describedby="instruments">
        <thead>
          <tr>
            <th scope="row"><span v-text="t$('global.field.id')"></span></th>
            <th scope="row"><span v-text="t$('aniScanApp.instrument.secCode')"></span></th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="instrument in instruments" :key="instrument.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'InstrumentView', params: { instrumentId: instrument.id } }">{{ instrument.id }}</router-link>
            </td>
            <td>{{ instrument.secCode }}</td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'InstrumentView', params: { instrumentId: instrument.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.view')"></span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'InstrumentEdit', params: { instrumentId: instrument.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.edit')"></span>
                  </button>
                </router-link>
                <b-button
                  v-on:click="prepareRemove(instrument)"
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
      </table>
    </div>
    <b-modal ref="removeEntity" id="removeEntity">
      <template #modal-title>
        <span id="aniScanApp.instrument.delete.question" data-cy="instrumentDeleteDialogHeading" v-text="t$('entity.delete.title')"></span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-instrument-heading" v-text="t$('aniScanApp.instrument.delete.question', { id: removeId })"></p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-text="t$('entity.action.cancel')" v-on:click="closeDialog()"></button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-instrument"
            data-cy="entityConfirmDeleteButton"
            v-text="t$('entity.action.delete')"
            v-on:click="removeInstrument()"
          ></button>
        </div>
      </template>
    </b-modal>
  </div>
</template>

<script lang="ts" src="./instrument.component.ts"></script>
