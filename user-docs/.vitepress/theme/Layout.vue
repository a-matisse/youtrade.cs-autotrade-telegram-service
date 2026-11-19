<script setup lang="ts">
import DefaultTheme from 'vitepress/theme'
import SiteHeader from './SiteHeader.vue'
</script>

<template>
  <DefaultTheme.Layout>
    <template #layout-top><SiteHeader/></template>
  </DefaultTheme.Layout>
</template>
