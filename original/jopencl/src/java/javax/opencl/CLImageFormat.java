package javax.opencl;

/**
 * 
 * @author Johan Henriksson
 *
 */
public class CLImageFormat
	{
  int image_channel_order;
  int image_channel_data_type;
  
	public CLImageFormat(int image_channel_order, int image_channel_data_type)
		{
		this.image_channel_order = image_channel_order;
		this.image_channel_data_type = image_channel_data_type;
		}

  
	}
