package tfar.woodcutter;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.filter.CompositeFilter;
import org.apache.logging.log4j.core.filter.ThresholdFilter;

public class WoodCutter implements ModInitializer, ClientModInitializer {

	public static final String MODID = "woodcutter";

	public static Block woodcutter;
	public static ScreenHandlerType<WoodCutterContainer> woodCutterContainer;
	public static RecipeSerializer<WoodcuttingRecipe> WOODCUTTING;

	@Override
	public void onInitialize() {
		woodcutter = Registry.register(Registry.BLOCK,new Identifier(MODID,MODID),new WoodCutterBlock(AbstractBlock.Settings.copy(Blocks.STONECUTTER)));
		Registry.register(Registry.ITEM,new Identifier(MODID,MODID),new BlockItem(woodcutter,new Item.Settings().group(ItemGroup.DECORATIONS)));
		woodCutterContainer = Registry.register(Registry.SCREEN_HANDLER,new Identifier(MODID,MODID) , new ScreenHandlerType<>(WoodCutterContainer::new));
		WOODCUTTING = RecipeSerializer.register(WoodCutter.MODID +":woodcutting", new WoodcuttingRecipe.Serializer2<>(WoodcuttingRecipe::new));
	}

	@Override
	public void onInitializeClient() {
		ScreenRegistry.register(woodCutterContainer,WoodCutterScreen::new);

		Filter logfilter = ((Logger) ClientRecipeBook.field_25622).getContext().getConfiguration().getFilter();
		Filter toRemove = null;
		if (logfilter instanceof CompositeFilter) {
			CompositeFilter compositeFilter = (CompositeFilter)logfilter;
			Filter[] filters  = compositeFilter.getFiltersArray();
			for (Filter filter : filters) {
				if (filter instanceof ThresholdFilter)
					toRemove = filter;
			}
		}
		if (toRemove != null) {
			((Logger) ClientRecipeBook.field_25622).getContext().removeFilter(toRemove);
			System.out.println("Log spam from Recipe Book successfully neutralized");
			((Logger) ClientRecipeBook.field_25622).getContext().addFilter(new ShutUpRecipeBookFilter());
		} else {
			System.out.println("Mission failed we'll get em next time");
		}
	}
}
