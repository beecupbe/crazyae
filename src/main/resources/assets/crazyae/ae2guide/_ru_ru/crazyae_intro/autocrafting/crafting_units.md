---
navigation:
  title: "Сопроцессоры и хранилища крафта"
  parent: crazyae_intro/autocrafting/autocrafting.md
  icon: crazyae:4x_crafting_accelerator
  position: 3
item_ids:
  - 4x_crafting_accelerator
  - 16x_crafting_accelerator
  - 64x_crafting_accelerator
  - 256x_crafting_accelerator
  - 1024x_crafting_accelerator
  - 4096x_crafting_accelerator
  - 16384x_crafting_accelerator
  - 65536x_crafting_accelerator
  - 262144x_crafting_accelerator
  - 1048576x_crafting_accelerator
  - 4194304x_crafting_accelerator
  - creative_crafting_accelerator
  - 1mb_crafting_storage
  - 4mb_crafting_storage
  - 16mb_crafting_storage
  - 64mb_crafting_storage
  - 256mb_crafting_storage
  - 1gb_crafting_storage
  - 2gb_crafting_storage
  - 8gb_crafting_storage
  - 32gb_crafting_storage
  - 128gb_crafting_storage
  - creative_crafting_storage
---

# Сопроцессоры и хранилища крафта

Усиленные версии блоков крафта AE2. Работают везде, где работают обычные:
в многоблочном [процессоре крафта](ae2:items-blocks-machines/crafting_cpu_multiblock.md),
в [объединителе блоков крафта](crafting_units_combiner.md) и в [квантовом процессоре](quantum_cpu.md).

## Блоки сопроцессоров

Число в названии — сколько потоков добавляет блок. Больше потоков — больше шаблонов отправляется за такт (тик).

<ItemGrid>
  <ItemIcon id="4x_crafting_accelerator" />
  <ItemIcon id="16x_crafting_accelerator" />
  <ItemIcon id="64x_crafting_accelerator" />
  <ItemIcon id="256x_crafting_accelerator" />
  <ItemIcon id="1024x_crafting_accelerator" />
  <ItemIcon id="4096x_crafting_accelerator" />
  <ItemIcon id="16384x_crafting_accelerator" />
  <ItemIcon id="65536x_crafting_accelerator" />
  <ItemIcon id="262144x_crafting_accelerator" />
  <ItemIcon id="1048576x_crafting_accelerator" />
  <ItemIcon id="4194304x_crafting_accelerator" />
  <ItemIcon id="creative_crafting_accelerator" />
</ItemGrid>

Каждый уровень крафтится из блоков предыдущего уровня, первый — из блока сопроцессора AE2.

<Row>
  <RecipeFor id="4x_crafting_accelerator" />
  <RecipeFor id="16x_crafting_accelerator" />
</Row>

## Хранилища крафта

От хранилища зависит, насколько большое задание примет процессор: от 1 МБ до 128 ГБ.
Каждое собирается из блока крафта AE2 и компонента хранения того же размера.

<ItemGrid>
  <ItemIcon id="1mb_crafting_storage" />
  <ItemIcon id="4mb_crafting_storage" />
  <ItemIcon id="16mb_crafting_storage" />
  <ItemIcon id="64mb_crafting_storage" />
  <ItemIcon id="256mb_crafting_storage" />
  <ItemIcon id="1gb_crafting_storage" />
  <ItemIcon id="2gb_crafting_storage" />
  <ItemIcon id="8gb_crafting_storage" />
  <ItemIcon id="32gb_crafting_storage" />
  <ItemIcon id="128gb_crafting_storage" />
  <ItemIcon id="creative_crafting_storage" />
</ItemGrid>

<Row>
  <RecipeFor id="1mb_crafting_storage" />
  <RecipeFor id="4mb_crafting_storage" />
</Row>

Творческие блоки доступны только в творческом режиме игры и практически безлимитные.
