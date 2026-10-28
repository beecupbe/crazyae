---
navigation:
  title: "Переносные ячейки"
  parent: crazyae_intro/storage/storage.md
  icon: crazyae:portable_cell_1mb
  position: 2
item_ids:
  - portable_cell_1mb
  - portable_cell_4mb
  - portable_cell_16mb
  - portable_cell_64mb
  - portable_cell_256mb
  - portable_cell_1gb
  - portable_cell_2gb
  - portable_cell_8gb
  - portable_cell_32gb
  - portable_cell_128gb
  - fluid_portable_cell_1mb
  - fluid_portable_cell_4mb
  - fluid_portable_cell_16mb
  - fluid_portable_cell_64mb
  - fluid_portable_cell_256mb
  - fluid_portable_cell_1gb
  - fluid_portable_cell_2gb
  - fluid_portable_cell_8gb
  - fluid_portable_cell_32gb
  - fluid_portable_cell_128gb
---

# Переносные ячейки

Ячейка хранения со встроенным терминалом: открывается прямо из руки, сеть не нужна.
Те же размеры, что у [обычных ячеек](storage_cells.md), — от 1 МБ до 128 ГБ.

<ItemGrid>
  <ItemIcon id="portable_cell_1mb" />
  <ItemIcon id="portable_cell_4mb" />
  <ItemIcon id="portable_cell_16mb" />
  <ItemIcon id="portable_cell_64mb" />
  <ItemIcon id="portable_cell_256mb" />
  <ItemIcon id="portable_cell_1gb" />
  <ItemIcon id="portable_cell_2gb" />
  <ItemIcon id="portable_cell_8gb" />
  <ItemIcon id="portable_cell_32gb" />
  <ItemIcon id="portable_cell_128gb" />
</ItemGrid>

<ItemGrid>
  <ItemIcon id="fluid_portable_cell_1mb" />
  <ItemIcon id="fluid_portable_cell_4mb" />
  <ItemIcon id="fluid_portable_cell_16mb" />
  <ItemIcon id="fluid_portable_cell_64mb" />
  <ItemIcon id="fluid_portable_cell_256mb" />
  <ItemIcon id="fluid_portable_cell_1gb" />
  <ItemIcon id="fluid_portable_cell_2gb" />
  <ItemIcon id="fluid_portable_cell_8gb" />
  <ItemIcon id="fluid_portable_cell_32gb" />
  <ItemIcon id="fluid_portable_cell_128gb" />
</ItemGrid>

## Энергия

Переносные ячейки работают на энергии AE. Зарядите их в <ItemLink id="ae2:charger" /> или возьмите заряженную во вкладке творческого режима.

## Автоподбор (ячейки для предметов)

- **Shift + ПКМ** ячейкой включает и выключает автоподбор.
- Пока он включён, подобранные предметы сразу попадают в ячейку (если она лежит в инвентаре и заряжена).
- Разметьте ячейку в <ItemLink id="ae2:cell_workbench" />, чтобы собирать только нужное — например, одни руды.

## Рецепт

Ячейка собирается из компонента хранения того же размера, корпуса ячейки, МЭ-сундука и плотной энергоячейки.

<Row>
  <RecipeFor id="portable_cell_1mb" />
  <RecipeFor id="fluid_portable_cell_1mb" />
</Row>
