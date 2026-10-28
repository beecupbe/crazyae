---
navigation:
  title: "Solar panels"
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

# Solar panels

<Row>
  <BlockImage id="basic_solar_panel" scale="3" />
  <BlockImage id="improved_solar_panel" scale="3" />
  <BlockImage id="advanced_solar_panel" scale="3" />
  <BlockImage id="perfect_solar_panel" scale="3" />
  <BlockImage id="quantum_solar_panel" scale="3" />
</Row>

Solar panels produce AE energy and give it straight to the ME network they touch — no energy acceptor needed.

- A panel works only when it **sees the sky** (nothing above it).
- At night it produces half as much.
- Each panel has a small internal buffer; the network takes energy from it.

| Panel | Day, AE/t | Night, AE/t | Buffer, AE |
|-------|-----------|-------------|------------|
| <ItemLink id="basic_solar_panel" /> | 128 | 64 | 100 000 |
| <ItemLink id="improved_solar_panel" /> | 512 | 256 | 400 000 |
| <ItemLink id="advanced_solar_panel" /> | 6 144 | 3 072 | 16 000 000 |
| <ItemLink id="perfect_solar_panel" /> | 32 768 | 16 384 | 64 000 000 |
| <ItemLink id="quantum_solar_panel" /> | 524 288 | 262 144 | 256 000 000 |

Numbers are defaults and can be changed in the config.

<Row>
  <RecipeFor id="basic_solar_panel" />
  <RecipeFor id="improved_solar_panel" />
</Row>
