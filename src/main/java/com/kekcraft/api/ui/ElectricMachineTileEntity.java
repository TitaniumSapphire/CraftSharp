package com.kekcraft.api.ui;

import java.io.IOException;

import com.kekcraft.ModPacket;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyConnection;
import cofh.api.energy.IEnergyReceiver;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class ElectricMachineTileEntity extends MachineTileEntity
		implements IEnergyReceiver, IEnergyConnection {
	public ElectricMachineTileEntity(int slots, int update) {
		super(slots, update);
	}

	protected EnergyStorage energy = new EnergyStorage(0);

	public EnergyStorage getEnergy() {
		return energy;
	}

	private int getEnergyCostPerCook(IMachineRecipe recipe) {
		return recipe.getFuelCost() / recipe.getCookTime();
	}

	@Override
	public void updateEntity() {
		if (!this.worldObj.isRemote) {
			if (canSmelt()) {
				IMachineRecipe recipe = getNextRecipe();
				if (recipe != null) {
					if (energy.getEnergyStored() - recipe.getFuelCost() >= 0) {
						beginSmeltNextItem();
						onItemConsumeStart();

						if (enablesAutomaticUpdates()) {
							ModPacket.sendTileEntityUpdate(this);
						}

						if (recipe.isInstant()) {
							energy.modifyEnergyStored(-recipe.getFuelCost());
							smeltItemWhenDone();
						}
					}
				}
			}
			if (isBurningRecipe()) {
				if (!currentRecipe.satifies(slots)) {
					reset();
					onSmeltingStopped();
					ModPacket.sendTileEntityUpdate(this);
				} else {
					currentCookTime--;

					energy.modifyEnergyStored(-getEnergyCostPerCook(currentRecipe));

					if (enablesAutomaticUpdates()) {
						cookTicks++;
						if (cookTicks >= tickUpdateRate) {
							cookTicks = 0;
							ModPacket.sendTileEntityUpdate(this);
						}
					}
					if (currentCookTime == 0) {
						smeltItemWhenDone();
					}
				}
			}
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound tagCompound) {
		super.readFromNBT(tagCompound);

		energy.setEnergyStored(tagCompound.getInteger("Energy"));
		energy.setMaxTransfer(tagCompound.getInteger("TransferRate"));
		energy.setCapacity(tagCompound.getInteger("MaxEnergy"));
	}

	@Override
	public void writeToNBT(NBTTagCompound tagCompound) {
		super.defaultWriteToNBT(tagCompound);

		tagCompound.setInteger("Energy", energy.getEnergyStored());
		tagCompound.setInteger("TransferRate", energy.getMaxExtract());
		tagCompound.setInteger("MaxEnergy", energy.getMaxEnergyStored());
	}

	@Override
	public int getEnergyStored(ForgeDirection from) {
		return energy.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection from) {
		return energy.getMaxEnergyStored();
	}

	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
		return getEnergy().receiveEnergy(maxReceive, simulate);
	}

	@Override
	public void read(ByteBufInputStream in) throws IOException {
		getEnergy().setEnergyStored(in.readInt());
		setCurrentCookTime(in.readInt());
		setCookTime(in.readInt());
	}

	@Override
	public void write(ByteBufOutputStream out) throws IOException {
		out.writeInt(xCoord);
		out.writeInt(yCoord);
		out.writeInt(zCoord);

		out.writeInt(getEnergy().getEnergyStored());
		out.writeInt(getCurrentCookTime());
		out.writeInt(getCookTime());
	}
}