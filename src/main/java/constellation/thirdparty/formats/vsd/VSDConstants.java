package constellation.thirdparty.formats.vsd;

/**
 * Represents a collection of Microsoft VISIO/VSD Entity Type Constants
 * @author lawrence.daniels@gmail.com
 */
public interface VSDConstants {
	// VSD Document Header
	byte[] VSD_HEADER = {
		(byte)0xD0, (byte)0xCF, (byte)0x11, (byte)0xE0, 
		(byte)0xA1, (byte)0xB1, (byte)0x1A, (byte)0xE1
	};
	
	// VSD File Positions
	long VSD_POS_COMPANY_NAME	= 0x0F08; 	// (0x0F08 = 3848)
	long VSD_POS_USER_NAME		= 0x12B4;	// (0x12B4 = 4788)
	long VSD_POS_VST_PATH		= 4840;
	long VSD_OFFSET_WORK_SHEETS	= 48; 
	
	// VSD Codes
	short VSD_CODE_EMPTY = 0x6F10;
	
	// VSD Document Separators
	String VSD_DOC_SUMMARY_INFO	= "D.o.c.u.m.e.n.t.S.u.m.m.a.r.y.I.n.f.o.r.m.a.t.i.o.n.";
	String VSD_ROOT_ENTRY 		= "R.o.o.t. .E.n.t.r.y.";
	String VSD_SUMMARY_INFO		= "S.u.m.m.a.r.y.I.n.f.o.r.m.a.t.i.o.n.";
	String VSD_VISIO_DOCUMENT	= "V.i.s.i.o.D.o.c.u.m.e.n.t.";
	String VSD_VISIO_DRAWING	= "V.I.S.I.O...D.r.a.w.i.n.g";
	String VSD_VISIO_INFO		= "V.i.s.i.o.I.n.f.o.r.m.a.t.i.o.n.";
		
}
