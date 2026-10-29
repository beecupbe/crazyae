---
navigation:
  title: "Солнечные панели"
  parent: crazyae_intro/energy/energy.md
  icon: crazyae:quantum_solar_panel
  position: 2
item_ids:
  - basic_solar_panel
  - improved_solar_panel
  - advanced_solar_panel
  - perfect_solar_panel
  - quantum_solar_panel
---

# Солнечные панели

<Row>
  <BlockImage id="basic_solar_panel" scale="3" />
  <BlockImage id="improved_solar_panel" scale="3" />
  <BlockImage id="advanced_solar_panel" scale="3" />
  <BlockImage id="perfect_solar_panel" scale="3" />
  <BlockImage id="quantum_solar_panel" scale="3" />
</Row>

Солнечные панели вырабатывают энергию AE и отдают её прямо в МЭ-сеть, к которой примыкают, — приёмник энергии не нужен.

- Панель работает, только если **видит небо** (над ней ничего нет).
- Ночью выработка вдвое меньше.
- У каждой панели есть небольшой внутренний буфер, из которого сеть забирает энергию.

| Панель | День, AE/т | Ночь, AE/т | Буфер, AE |
|--------|------------|------------|-----------|
| <ItemLink id="basic_solar_panel" /> | 128 | 64 | 100 000 |
| <ItemLink id="improved_solar_panel" /> | 512 | 256 | 400 000 |
| <ItemLink id="advanced_solar_panel" /> | 6 144 | 3 072 | 16 000 000 |
| <ItemLink id="perfect_solar_panel" /> | 32 768 | 16 384 | 64 000 000 |
| <ItemLink id="quantum_solar_panel" /> | 524 288 | 262 144 | 256 000 000 |

Это значения по умолчанию, их можно изменить в конфиге.

<Row>
  <RecipeFor id="basic_solar_panel" />
  <RecipeFor id="improved_solar_panel" />
</Row>
