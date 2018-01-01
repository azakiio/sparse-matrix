import java.util.*;
// NOTE: You do not need to modify this class, but you should understand this class since 
//       SparseMatrix should return exactly the same results.
public class SparseMatrix extends Matrix {

	private HashMap<Integer,HashMap <Integer,Double>> _hmRow2Col2Val; 
	// Do not assign (=) anything here... this is invoked *after* super()
	                             // and assigning something here will override any effects of init(...)
	                             // called by super().

	public SparseMatrix(int rows, int cols) throws MatrixException {
		super(rows, cols);
	}

	public SparseMatrix(String filename) throws MatrixException {
		super(filename);
	}

	@Override
	public void init(int rows, int cols) {
		_hmRow2Col2Val = new HashMap<Integer, HashMap <Integer, Double>>();
	}

	@Override
	public double get(int i, int j) throws MatrixException {
		// Bounds check
		if (i < 0 || j < 0 || i >= getNumRows() || j >= getNumCols())
			throw new MatrixException("get(" + i + "," + j + ") not in matrix bounds " + 
					getNumRows() + " X " + getNumCols());	
		
		// If legal bounds, return value
		if(_hmRow2Col2Val.containsKey(i)){
			if(_hmRow2Col2Val.get(i).containsKey(j)){
				return(_hmRow2Col2Val.get(i).get(j));
				
			}
		}
		return 0.0;
	}

	@Override
	public void set(int i, int j, double val) throws MatrixException {
		// Bounds check
		if (i < 0 || j < 0 || i >= getNumRows() || j >= getNumCols())
			throw new MatrixException("set(" + i + "," + j + ") not in matrix bounds " + 
					getNumRows() + " X " + getNumCols());
		
		// If legal bounds, set value
		if(!_hmRow2Col2Val.containsKey(i))
			_hmRow2Col2Val.put(i, new HashMap<Integer, Double>());
		_hmRow2Col2Val.get(i).put(j, val);
	}

	@Override
	public Matrix transpose() throws MatrixException {
		
		Matrix result = new SparseMatrix(getNumCols(), getNumRows());
		
//		// Perform the transposition
//		for (int row = 0; row < getNumRows(); row++) {
//			for (int col = 0; col < getNumCols(); col++) {
//				result.set(col, row, get(row, col));
//			}
		for(Integer i : this._hmRow2Col2Val.keySet()){
			HashMap<Integer, Double> temp = this._hmRow2Col2Val.get(i);
			for(Integer j : temp.keySet()){
				Double d = temp.get(j);
				result.set(j,i,d);
			}
		}
		return result;
	}

	@Override
	public Matrix multiply(Matrix b) throws MatrixException {
		
		// Check for proper operand dimensions
		if (this.getNumCols() != b.getNumRows())
			throw new MatrixException("Dimension mismatch for multiply: " + this.getNumCols() 
				+ " cols in op1 and " + b.getNumRows() + " rows in op2");
		
		// Return type will be same matrix type as "this"
		// NOTE: you can also assume that "this" and "b" are of the same type
		Matrix prod = new SparseMatrix(this.getNumRows(), b.getNumCols());
		SparseMatrix sb = (SparseMatrix) b.transpose();

			for(Integer i : _hmRow2Col2Val.keySet()){
				for(Integer j : sb._hmRow2Col2Val.keySet()){
					double sum = 0d;
					for(Integer k : _hmRow2Col2Val.get(i).keySet()){
						sum += _hmRow2Col2Val.get(i).get(k)*b.get(k, j);
						}
					prod.set(i, j, sum);	
					}
				}
			return prod;
		}
//		// Compute the inner product for every (row,col) in the new prod matrix
//		for (int row = 0; row < this.getNumRows(); row++)
//			for (int col = 0; col < b.getNumCols(); col++) {
//				// prod[row,col] = dot_product(this[row,:], b[:,col])
//				double sum = 0d;
//				for (int index = 0; index < this.getNumCols(); index++)
//					sum += this.get(row, index) * b.get(index, col);
//				prod.set(row, col, sum);
//			}
//		
//		return prod;
//	}

	@Override
	public Matrix multiply01(Matrix b) throws MatrixException {

		// Check for proper operand dimensions
		if (this.getNumCols() != b.getNumRows())
			throw new MatrixException("Dimension mismatch for multiply: " + this.getNumCols() 
				+ " cols in op1 and " + b.getNumRows() + " rows in op2");
		
		// Return type will be same matrix type as "this"
		Matrix prod = new SparseMatrix(this.getNumRows(), b.getNumCols());
		SparseMatrix sb = (SparseMatrix) b.transpose();

			for(Integer i : _hmRow2Col2Val.keySet()){
				for(Integer j : sb._hmRow2Col2Val.keySet()){
					double sum = 0d;
					for(Integer k : _hmRow2Col2Val.get(i).keySet()){
						sum += _hmRow2Col2Val.get(i).get(k)*b.get(k, j);
						}
					prod.set(i, j, sum >= 1d ? 1d : 0d);	
					}
				}
			return prod;
	}
//		// Compute the inner product for every (row,col) in the new prod matrix
//		for (int row = 0; row < this.getNumRows(); row++)
//			for (int col = 0; col < b.getNumCols(); col++) {
//				// Only difference between multiply and multiply01 is that multiply01
//				// converts all numbers >= 1 to 1 and otherwise to 0.
//				double sum = 0d;
//				for (int index = 0; index < this.getNumCols(); index++)
//					sum += this.get(row, index) * b.get(index, col);
//				prod.set(row, col, sum >= 1d ? 1d : 0d);
//			}
//		
//		return prod;
//	}

	// NOTE: some simple tests... SparseMatrix should produce exactly the same results,
	//       only much faster and in much less space for large, sparse matrices.
	public static void main(String[] args) throws Exception {
		
		final boolean SHOW_RESULTS = true;
		
		SparseMatrix d = new SparseMatrix("smatrix1.txt");
		//DenseMatrix d = new DenseMatrix("dmatrix2.txt");
		//DenseMatrix d = new DenseMatrix("smatrix1.txt"); // NOTE: sparse encoding of dmatrix1.txt
		//DenseMatrix d = new DenseMatrix("smatrix2.txt"); // NOTE: too large to load as dense matrix
		if (SHOW_RESULTS) System.out.println(d);

		Matrix dt = d.transpose();
		if (SHOW_RESULTS) System.out.println("Transpose:\n" + dt);

		Matrix dmult = d.multiply(dt);
		if (SHOW_RESULTS) System.out.println("Self-multiply:\n" + dmult);

		Matrix dmult01 = d.multiply01(dt);
		if (SHOW_RESULTS) System.out.println("Self-01-multiply:\n" + dmult01);
		
		System.out.println("DMult[3,3] = " + dmult.get(3, 3));
	}

}
