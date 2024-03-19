<template>
  <!-- The AG Grid component -->
  <!--  <div style="display: flex; flex-direction: column; height: 100%">-->
  <!--    <div style="width: 100%; flex: 1 1 auto;">-->
  <div style="height: 90vh">
    <ag-grid-vue
      :getRowId="getRowId"
      :rowData="rowData"
      :columnDefs="colDefs"
      :style="{ width, height }"
      :class="themeClass"
      :rowHeight="rowHeight"
      :rowSelection="rowSelection"
      :defaultColDef="defaultColDef"
      @grid-ready="onGridReady"
    >
    </ag-grid-vue>
  </div>
  <!--  </div>-->
</template>

<script lang="ts" src="./robot-new.component.ts"></script>
