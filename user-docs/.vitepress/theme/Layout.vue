<script setup lang="ts">
import DefaultTheme from 'vitepress/theme'
import SiteHeader from './SiteHeader.vue'
import SiteFooter from './SiteFooter.vue'
</script>

<template>
  <DefaultTheme.Layout>
    <template #layout-top><SiteHeader/></template>
    <template #layout-bottom><SiteFooter/></template>
  </DefaultTheme.Layout>
</template>
