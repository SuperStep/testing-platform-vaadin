package com.sust.testing.platform.ui.components;

import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import de.mekaso.vaadin.addons.Carousel;
import de.mekaso.vaadin.addons.CarouselModel;

@Tag("mekaso-carousel")
@JsModule("./carousel.js")
public class CustomCarousel extends PolymerTemplate<CarouselModel> implements HasComponents, HasSize {
	private static final String CAROUSEL_CELL = "carousel__cell";
	private static final long serialVersionUID = 1L;
	
	private static CustomCarousel instance;
	public static CustomCarousel create() {
		instance = new CustomCarousel();
		return instance;
	}
	
	private CustomCarousel() {}
	
	/**
	 * Creates a border around the center stage of the carousel.
	 * @return the instance
	 */
	public CustomCarousel withBorder() {
		instance.getModel().setBorder(true);
		return instance;
	}

	public CustomCarousel withoutBorder() {
		instance.getModel().setBorder(false);
		return instance;
	}
	
	/**
	 * Automatic change of slides.
	 * @return the instance
	 */
	public CustomCarousel withAutoplay() {
		instance.getModel().setAutoplay(true);
		return instance;
	}
	
	/**
	 * Stop the autoplay.
	 * @return the instance
	 */
	public CustomCarousel stop() {
		instance.getModel().setAutoplay(false);
		return instance;
	}
	
	/**
	 * Configure the duration of slide showing (default is 5 seconds).
	 * Value is only important when autoplay is enabled.
	 * @param duration the duration
	 * @param unit time unit
	 * @return the instance
	 */
	public CustomCarousel withDuration(long duration, TimeUnit unit) {
		instance.getModel().setDuration((int) unit.toMillis(duration));
		return instance;
	}
	
	/**
	 * The carousel's orientation (default is horizontal). 
	 * @param horizontal true for horizontal carousel
	 * @return the instance
	 */
	public CustomCarousel withHorizontalOrientation(boolean horizontal) {
		this.getModel().setIsHorizontal(horizontal);
		return instance;
	}
	
	/**
	 * Turn to the next component.
	 */
	public void next() {
		this.getElement().callJsFunction("next");
	}
	
	/**
	 * Turn to the previous component.
	 */
	public void prev() {
		this.getElement().callJsFunction("prev");
	}
	
	/**
	 * Turn the carousel to one special component.
	 * @param component the component for the center stage
	 */
	public void show(Component component) {
		int index = this.getChildren().collect(Collectors.toList()).indexOf(component);
		if (index > -1) {
			this.getModel().setSelectedIndex(index);
		}
	}
	
	/**
	 * Turn the carousel to the component by index.
	 * @param index the component index
	 */
	public void show(int index) {
		if (index > 0 && index < this.getChildren().count()) {
			this.getModel().setSelectedIndex(index);
		}
	}

	@Override
	public void add(Component... components) {
		if (components != null && components.length > 0) {
			for (int i = 0; i < components.length; i++) {
				Component comp = getOrWrapComponent(components[i]);
				HasComponents.super.add(comp);
			}
		}
		updateUI();
	}
	
	@Override
	public void add(String text) {
		Div wrapper = new Div();
		wrapper.addClassName(CAROUSEL_CELL);
		wrapper.add(text);
		HasComponents.super.add(wrapper);
		updateUI();
	}
	
	@Override
	public void addComponentAsFirst(Component component) {
		HasComponents.super.addComponentAsFirst(getOrWrapComponent(component));
		updateUI();
	}
	
	@Override
	public void addComponentAtIndex(int index, Component component) {
		HasComponents.super.addComponentAtIndex(index, getOrWrapComponent(component));
		updateUI();
	}
	
	@Override
	public void remove(Component... components) {
		HasComponents.super.remove(components);
		updateUI();
	}
	
	private void updateUI() {
		int cellCount = (int) this.getChildren().count();
		this.getModel().setCellCount(cellCount);
		if (this.getWidth() != null) {
			this.getModel().setWidth(this.getWidth());
		}
		if (this.getHeight() != null) {
			this.getModel().setHeight(this.getHeight());
		}
	}
	
	private Component getOrWrapComponent(Component original) {
		if (original instanceof Div) {
			((Div) original).addClassName(CAROUSEL_CELL);
			return original;
		} else {
			Div wrapper = new Div();
			wrapper.addClassName(CAROUSEL_CELL);
			wrapper.add(original);
			return wrapper;
		}
	}
}