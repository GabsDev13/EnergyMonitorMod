package net.gabsdev.energymonitor;

import net.minecraftforge.registries.ForgeRegistries;


import java.util.Iterator;
import java.util.Map;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.LevelTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.event.level.BlockEvent;


@Mod.EventBusSubscriber
public class ServerEvents{

    ItemStackHandler itemHandler = new ItemStackHandler(3); // Furnace has 3 slots: input, fuel, output
    
    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        // Verifica se o bloco colocado é uma fornalha
        BlockState placedBlockState = event.getPlacedBlock();
        BlockPos pos = event.getPos();
        net.minecraft.world.level.block.Block placedBlock = placedBlockState.getBlock();
        
        if(event.getLevel().isClientSide()) {
            return; // Ignore client side
        }

        ServerLevel level = (ServerLevel) event.getLevel();

        ServerLevel overworld = level.getServer().getLevel(ServerLevel.OVERWORLD); // Acessa o overworld
        if(overworld == null) return;

        // Acessa o SavedData
        FurnacesData furnaceData = overworld.getDataStorage()
        .computeIfAbsent(FurnacesData::new, FurnacesData::new, "furnace_data");
        // Computa se ausente, cria uma nova instância de FurnacesData se não existir
        //São duas FurnacesData::new porque o primeiro é o Supplier para carregar os dados existentes
        //e o segundo é o Supplier para criar novos dados se não houver dados existentes
        //Arquivo gerado: world/data/furnace_data.dat

        // Se for uma fornalha, adiciona ao rastreamento
        if(placedBlock == Blocks.FURNACE) {
             //trackedFurnaces.put(pos, 0);
            furnaceData.setCount(pos, 0); // Adiciona a fornalha ao SavedData
            System.out.println("Furnace placed at " + pos);

        }

        /*
        Algumas ressalvas importantes sobre as classes e métodos usados:
        - BlockEvent.EntityPlaceEvent: Evento disparado quando uma entidade (jogador, criatura, etc.) coloca um bloco no mundo.
        - BlockState: Representa o estado de um bloco no mundo, incluindo o tipo de bloco e suas propriedades.
        - BlockPos: Representa uma posição tridimensional no mundo do Minecraft.
        - ServerLevel: Representa um nível (mundo) no servidor, permitindo acesso a dados e entidades do mundo.
        - DataStorage: Usado para armazenar dados persistentes no mundo do Minecraft.
        - computeIfAbsent: Método que verifica se uma chave existe no armazenamento de dados; se não existir, cria um novo valor usando o Supplier fornecido.
        - Blocks.FURNACE: Representa o bloco de fornalha no Minecraft.
        - é vantajoso usar o SavedData para rastrear fornalhas porque os dados persistem entre reinicializações do servidor, permitindo monitorar o uso das fornalhas ao longo do tempo.
          sem escanear o mundo toda vez que o servidor reinicia. Forge, de certa forma, protege métodos que usam chunks. 
         */
        
    }

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent event) {

        if(event.level.isClientSide()) {
            return; // Ignore client side
        }

        if(event.phase == TickEvent.Phase.END) return; // Process only at the start of the tick

        ServerLevel level = (ServerLevel) event.level;

        ServerLevel overworld = level.getServer().getLevel(ServerLevel.OVERWORLD);
        if(overworld == null) return;

        FurnacesData furnaceData = overworld.getDataStorage()
        .computeIfAbsent(FurnacesData::new, FurnacesData::new, "furnace_data");

        // Itera sobre as fornalhas rastreadas
        // Rastrea-se as fornalhas pelo BlockPos e o número de itens no slot de saída
        //BlockPos das fornalhas foram extraídas do SavedData
        Iterator<Map.Entry<BlockPos, Integer>> iterator = furnaceData.getTracked().entrySet().iterator();

        while(iterator.hasNext()) { // Itera sobre cada fornalha rastreada
            Map.Entry<BlockPos, Integer> entry = iterator.next();
            BlockPos pos = entry.getKey(); // Pega a posição da fornalha.
            
            BlockEntity be = level.getBlockEntity(pos);// Pega a BlockEntity na posição da fornalha.
            if(be instanceof FurnaceBlockEntity furnace) {  // Verifica se a BlockEntity é uma fornalha
                ItemStack currentOutput = furnace.getItem(2); // Pega o item no slot de saída (slot 2) da fornalha

                if(!currentOutput.isEmpty()) { // Se o slot de saída não estiver vazio

                    int lastCount = furnaceData.getCount(pos); // Pega a última contagem registrada para essa fornalha
                    int delta = currentOutput.getCount() - lastCount; // Calcula a diferença entre a contagem atual e a última contagem
                    if(delta > 0) { // Se a diferença for maior que zero, significa que novos itens foram produzidos
                        ResourceLocation id = ForgeRegistries.ITEMS.getKey(currentOutput.getItem()); // Pega o ID do item produzido
                        if(id != null){// Verifica se o ID do item não é nulo
                            System.out.println("Item smelted: " + id.getPath() + " x" + delta + " at " + pos);
                        }
                        furnaceData.setCount(pos, currentOutput.getCount());// Atualiza a contagem registrada para essa fornalha
                    }
                } else {
                        furnaceData.setCount(pos, 0); // Se o slot de saída estiver vazio, reseta a contagem para zero
                } // ele não dispara infinidamente porque só atualiza a contagem quando há uma mudança no slot de saída
                // Assim, se nada for produzido, a contagem permanece a mesma e não há saída repetida
                // Isso está explicito na linha de código acima: int delta = currentOutput.getCount() - lastCount;
                // Só há saída quando delta > 0
            }
        }
    }


    // Esse evento pode ser usado com BlockEntities do Mekanism, que será introduzido futuramente
    //Adiantando: se usa IEnergyContainer para acessar energia
    //IEnergyContainer pode funcionar semelhante à saida de itens da fornalha, onde se monitora mudanças na energia armazenada
}