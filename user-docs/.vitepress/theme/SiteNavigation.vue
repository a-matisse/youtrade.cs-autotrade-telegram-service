<script setup lang="ts">
import {siteNavigation, siteOrigin, themedSiteLink} from '../../../shared/site/navigation'
import type {SiteTheme} from '../../../shared/site/theme'
defineProps<{theme: SiteTheme}>()
</script>

<template>
  <nav class="site-nav" aria-label="Разделы сайта"><a v-for="item in siteNavigation" :key="item.href" :href="themedSiteLink(item.href.startsWith('/') ? siteOrigin + item.href : item.href, theme)" :aria-current="'section' in item && item.section === 'learning' ? 'page' : undefined">{{ item.label }}</a></nav>
</template>
