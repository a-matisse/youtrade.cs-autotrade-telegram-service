<script setup lang="ts">
import {computed, onMounted, onUnmounted, ref, watch} from 'vue'
import {useData, useRoute} from 'vitepress'
import {siteOrigin, themedSiteLink} from '../../../shared/site/navigation'
import {applySiteTheme, readSiteTheme} from '../../../shared/site/theme'
import icons from '../../../shared/site/icons.svg?no-inline'
import SiteNavigation from './SiteNavigation.vue'
import {bindHeaderMenus, closeHeaderMenus} from '../../../shared/site/menus'

const {isDark} = useData()
const route = useRoute()
const header = ref<HTMLElement>()
let unbindMenus: (() => void) | undefined
const theme = computed(() => isDark.value ? 'dark' : 'light')
const link = (href: string) => themedSiteLink(href.startsWith('/') ? siteOrigin + href : href, theme.value)
onMounted(() => {
  isDark.value = readSiteTheme() === 'dark'
  if (header.value) unbindMenus = bindHeaderMenus(header.value)
})
onUnmounted(() => unbindMenus?.())
watch(isDark, () => applySiteTheme(theme.value))
watch(() => route.path, () => {if (header.value) closeHeaderMenus(header.value)})
</script>

<template>
  <header ref="header" class="site-header">
    <div class="site-header-inner">
      <a class="site-brand" :href="link('/')" aria-label="Y.CS — на главную"><img src="/logo.png" alt=""><span>Y.CS</span></a>
      <SiteNavigation :theme="theme"/>
      <div class="site-actions">
        <button class="site-theme-toggle" type="button" :aria-label="isDark ? 'Включить светлую тему' : 'Включить тёмную тему'" @click="isDark = !isDark"><svg aria-hidden="true"><use :href="`${icons}#${isDark ? 'sun' : 'moon'}`"/></svg></button>
        <a class="site-account-link" :href="link('/dashboard')">Кабинет</a>
        <details class="site-mobile-nav"><summary>Меню</summary><SiteNavigation :theme="theme"/></details>
      </div>
    </div>
  </header>
</template>
