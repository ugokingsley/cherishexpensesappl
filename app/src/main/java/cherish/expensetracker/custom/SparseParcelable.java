package cherish.expensetracker.custom;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseBooleanArray;


public class SparseParcelable extends SparseBooleanArray implements Parcelable {

    public static Parcelable.Creator<SparseParcelable> CREATOR = new Parcelable.Creator<SparseParcelable>() {
        @Override
        public SparseParcelable createFromParcel(Parcel source) {
            SparseParcelable read = new SparseParcelable();
            int size = source.readInt();

            int[] keys = new int[size];
            boolean[] values = new boolean[size];

            source.readIntArray(keys);
            source.readBooleanArray(values);

            for (int i = 0; i < size; i++) {
                read.put(keys[i], values[i]);
            }

            return read;
        }

        @Override
        public SparseParcelable[] newArray(int size) {
            return new SparseParcelable[size];
        }
    };

    public SparseParcelable() {

    }

    public SparseParcelable(SparseBooleanArray sparseBooleanArray) {
        for (int i = 0; i < sparseBooleanArray.size(); i++) {
            this.put(sparseBooleanArray.keyAt(i), sparseBooleanArray.valueAt(i));
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        int[] keys = new int[size()];
        boolean[] values = new boolean[size()];

        for (int i = 0; i < size(); i++) {
            keys[i] = keyAt(i);
            values[i] = valueAt(i);
        }

        dest.writeInt(size());
        dest.writeIntArray(keys);
        dest.writeBooleanArray(values);
    }
}