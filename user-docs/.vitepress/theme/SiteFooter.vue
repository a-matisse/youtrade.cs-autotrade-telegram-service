<script setup lang="ts">
import {useData, withBase} from 'vitepress'
import {
  siteFooterDescription, siteFooterDisclaimer, siteFooterLinks, siteOrigin, telegramBotUrl, telegramGroupUrl, themedSiteLink,
} from '../../../shared/site/navigation'
import icons from '../../../shared/site/icons.svg?no-inline'
import '../../../shared/site/footer.css'

const {isDark} = useData()
const link = (href: string) => themedSiteLink(href.startsWith('/') ? siteOrigin + href : href, isDark.value ? 'dark' : 'light')
</script>

<template>
  <footer class="site-footer site-control-surface--dark">
    <div class="site-footer-inner">
      <div class="site-footer-identity">
        <a class="site-footer-brand" :href="link('/#start')" aria-label="Главная Y.CS">
          <img :src="withBase('/logo.png')" alt="">
        </a>
        <a class="site-control site-control--quiet site-control--icon" :href="telegramGroupUrl"
           aria-label="Группа Y.CS в Telegram" title="Группа Y.CS в Telegram">
          <svg aria-hidden="true"><use :href="`${icons}#telegram`"/></svg>
        </a>
      </div>
      <div class="site-footer-bot">
        <a class="site-footer-link" :href="telegramBotUrl">Telegram-бот</a>
      </div>
      <div class="site-footer-legal">
        <a v-for="{href, label} in siteFooterLinks" :key="href" class="site-footer-link" :href="link(href)">{{ label }}</a>
      </div>
      <div class="site-footer-notes">
        <p>{{ siteFooterDescription }}</p>
        <small>{{ siteFooterDisclaimer }}</small>
      </div>
    </div>
  </footer>
</template>
