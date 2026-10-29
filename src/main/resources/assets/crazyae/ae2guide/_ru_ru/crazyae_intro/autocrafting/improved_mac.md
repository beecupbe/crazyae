---
navigation:
  title: "Улучшенный молекулярный сборщик"
  parent: crazyae_intro/autocrafting/autocrafting.md
  icon: crazyae:improved_mac
  position: 4
item_ids:
  - improved_mac
---

# Улучшенный молекулярный сборщик

<BlockImage id="improved_mac" scale="4" />

[Молекулярный сборщик](ae2:items-blocks-machines/molecular_assembler.md), которому не нужен поставщик шаблонов.
Положите в него до **45 шаблонов крафта** и подключите к сети — все процессоры сразу смогут ими пользоваться.

- Без улучшений делает **8 крафтов за тик**.
- <ItemLink id="stacks_card" /> увеличивает это число. По умолчанию: 16 / 32 / 64 / 96 / 128 крафтов за тик с 1–5 картами
  (настраивается в `crazyae-machines.toml`).
- Принимает только шаблоны крафта (рецепты верстака). Для кузнечного стола, камнереза и огромного числа шаблонов есть [квантовый процессор](quantum_cpu.md).
- Результаты сразу уходят в сеть.

<RecipeFor id="improved_mac" />
