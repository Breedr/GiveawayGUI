/**
 * GiveawayTableModel.java
 * GiveawayGrowl
 *
 * Created by Ed George on 2 Jan 2015
 *
 */
package uk.breedr.notifier.giveaway.gui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import uk.breedr.notifier.giveaway.Giveaway;

/**
 * @author edgeorge
 *
 */
public class GiveawayTableModel extends AbstractTableModel{

	private static final long serialVersionUID = 1L;

	private List<Giveaway> giveaways;

	public GiveawayTableModel(){
		this.giveaways = new ArrayList<Giveaway>();
	}
	
	public GiveawayTableModel(List<Giveaway> giveaways){
		this.giveaways = new ArrayList<Giveaway>(giveaways);

		if(this.giveaways == null){
			this.giveaways = new ArrayList<Giveaway>();
		}

	}

	@Override
	public int getRowCount() {
		return giveaways.size();
	}


	@Override
	public int getColumnCount() {
		return 4;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {

		Object value = "??";
		Giveaway giveaway = giveaways.get(rowIndex);
		switch (columnIndex) {
		case 0:
			value = giveaway.getSubmission().getTitle();
			break;
		case 1:
			value = giveaway.getType().getSymbol();
			break;
		case 2:
			value = giveaway.getSubmission().getCreatedUtc().toString();
			break;
		case 3:
			value = giveaway.getSubmission().getShortURL();
			break;
		}
		return value;
	}

	public Giveaway getGiveawayAtRow(int row){
		if(row >= giveaways.size())
			return null;

		return giveaways.get(row);
	}

	public String getColumnName(int index) {
		switch(index){
		case 0:
			return "Title";
		case 1:
			return "Type";
		case 2:
			return "Created";
		default:
			return "URL";
		}
	}

	public void addRow(Giveaway newGiveaway) {
		giveaways.add(newGiveaway);
		fireTableRowsInserted(getRowCount() - 1, getRowCount() - 1);
	}


	public List<Giveaway> getGiveaways() {
		return giveaways;
	}

	//    public void deleteRow() {
	//           for(int rowIndex = giveaways.size() - 1; rowIndex >= 0; rowIndex--) {
	//            if(giveaways.get(rowIndex).isSelect()) {
	//          giveaways.remove(rowIndex);
	//         }
	//           }
	//     fireTableDataChanged();
	//    }

}
