<script setup lang="ts">
import {computed, onMounted, onUnmounted, ref} from 'vue'
import {bindTickerVisibility, observeSteamRates, repeatTickerRates, type SteamCurrencyRate} from '../../../shared/site/currency'

const ticker = ref<HTMLElement>()
const rates = ref<SteamCurrencyRate[]>([])
const repeatedRates = computed(() => repeatTickerRates(rates.value))
let stopRates: (() => void) | undefined
let stopVisibility: (() => void) | undefined
onMounted(() => {
  stopRates = observeSteamRates(value => {rates.value = value})
  if (ticker.value) stopVisibility = bindTickerVisibility(ticker.value)
})
onUnmounted(() => {stopRates?.(); stopVisibility?.()})
</script>

<template>
  <div ref="ticker" class="currency-ticker" aria-label="Расчётные курсы Steam">
    <div class="ticker-label">STEAM RATE</div>
    <div class="ticker-window">
      <div v-if="rates.length" class="ticker-track">
        <div v-for="copy in [0, 1]" :key="copy" class="ticker-group" :aria-hidden="copy === 1">
          <span v-for="(item, index) in repeatedRates" :key="`${item.currency}-${index}`" class="ticker-item" :title="item.source ? `Источник: ${item.source}` : undefined"><b>{{ item.currency }}</b><em>{{ item.rate.toFixed(2) }}</em></span>
        </div>
      </div>
      <span v-else class="ticker-empty">—</span>
    </div>
  </div>
</template>
