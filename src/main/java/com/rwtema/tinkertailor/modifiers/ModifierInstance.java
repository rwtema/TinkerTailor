package com.rwtema.tinkertailor.modifiers;

public final class ModifierInstance implements Comparable<ModifierInstance> {
	public final Modifier modifier;
	public final int level;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ModifierInstance that = (ModifierInstance) o;

		return modifier.equals(that.modifier);
	}

	@Override
	public int hashCode() {
		return modifier.hashCode();
	}

	public ModifierInstance(Modifier modifier, int level) {
		this.modifier = modifier;
		this.level = level;
	}

	@Override
	public int compareTo(ModifierInstance other) {
		int i = this.modifier.compareTo(other.modifier);
		if (i != 0) return i;
		return Double.compare(this.level, other.level);
	}
}
