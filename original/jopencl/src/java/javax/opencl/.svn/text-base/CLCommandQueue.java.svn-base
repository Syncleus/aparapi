package javax.opencl;

/**
 * OpenCL command queue
 * @author Johan Henriksson
 *
 */
public class CLCommandQueue extends OpenCL
	{
	int id;
	
	CLCommandQueue(CLContext context, CLDevice deviceID)
		{
		int ret=_createCommandQueue(context.id, deviceID.device_id);
		assertSuccess(ret);
		}

	
	public void retain()
		{
		int ret=_retain(id);
		assertSuccess(ret);
		}
	
	public void release()
		{
		int ret=_release(id);
		assertSuccess(ret);
		}
	
	
	
	private native int _createCommandQueue(int context, int deviceID);
	private native int _retain(int id);
	private native int _release(int id);
	private native int _flush(int cqid);
	private native int _finish(int cqid);
	private native int _enqueueReadBufferInt(int cqid, int mem, boolean blocking, int offset, int numElem, int[] buffer, int[] waitFor);
	private native int _enqueueReadBufferFloat(int cqid, int mem, boolean blocking, int offset, int numElem, float[] buffer, int[] waitFor);
	private native int _enqueueNDRangeKernel(int cqid, int kernel, int workDim, int[] globalOffset, int[] globalSize, int[] localSize, int[] waitFor);
	private native int _enqueueBarrier(int cqid);


	
	/*
	 * 
extern  cl_int 
clGetCommandQueueInfo(cl_command_queue      command_queue,
                      cl_command_queue_info param_name,
                      size_t                param_value_size,
                      void *                param_value,
                      size_t *              param_value_size_ret) ;

extern  cl_int 
clSetCommandQueueProperty(cl_command_queue              command_queue,
                          cl_command_queue_properties   properties, 
                          cl_bool                        enable,
                          cl_command_queue_properties * old_properties) ;



	 */
	
	
	public void flush()
		{
		int ret=_flush(id);
		assertSuccess(ret);
		}
	
	
	/**
	 * Blocks until all previously queued OpenCL commands in command queue are issued to the
	 * associated device and have completed. clFinish does not return until all queued commands in 
	 * command queue have been processed and completed. clFinish is also a synchronization point.
	 */ 
	public void finish()
		{
		int ret=_finish(id);
		assertSuccess(ret);
		}
	

	private int[] waitforToID(CLEvent[] waitFor)
		{
		if(waitFor==null)
			return null;
		int[] await=new int[waitFor.length];
		for(int i=0;i<await.length;i++)
			await[i]=waitFor[i].id;
		return await;
		}
	
	public void enqueueReadBuffer(CLMem mem, boolean blocking, int offset, int numElem, int[] buffer, CLEvent[] waitFor)
		{
		int ret=_enqueueReadBufferInt(id, mem.id, blocking, offset, numElem, buffer, waitforToID(waitFor));
		assertSuccess(ret);
		}

	public void enqueueReadBuffer(CLMem mem, boolean blocking, int offset, int numElem, float[] buffer, CLEvent[] waitFor)
		{
		int ret=_enqueueReadBufferFloat(id, mem.id, blocking, offset, numElem, buffer, waitforToID(waitFor));
		assertSuccess(ret);
		}

	

	/*
                           
extern  cl_int 
clEnqueueWriteBuffer(cl_command_queue   command_queue, 
                    cl_mem             buffer, 
                    cl_bool            blocking_write, 
                    size_t             offset, 
                    size_t             cb, 
                    const void *       ptr, 
                    cl_uint            num_events_in_wait_list, 
                    const cl_event *   event_wait_list, 
                    cl_event *         event) ;
                           
extern  cl_int 
clEnqueueCopyBuffer(cl_command_queue    command_queue, 
                   cl_mem              src_buffer,
                   cl_mem              dst_buffer, 
                   size_t              src_offset,
                   size_t              dst_offset,
                   size_t              cb, 
                   cl_uint             num_events_in_wait_list,
                   const cl_event *    event_wait_list,
                   cl_event *          event) ;
                */

	
	/*
extern  cl_int 
clEnqueueReadImage(cl_command_queue     command_queue,
                  cl_mem               image,
                  cl_bool              blocking_read, 
                  const size_t *       origin[3],
                  const size_t *       region[3],
                  size_t               row_pitch,
                  size_t               slice_pitch, 
                  void *               ptr,
                  cl_uint              num_events_in_wait_list,
                  const cl_event *     event_wait_list,
                  cl_event *           event) ;

extern  cl_int 
clEnqueueWriteImage(cl_command_queue    command_queue,
                   cl_mem              image,
                   cl_bool             blocking_write, 
                   const size_t *      origin[3],
                   const size_t *      region[3],
                   size_t              input_row_pitch,
                   size_t              input_slice_pitch, 
                   const void *        ptr,
                   cl_uint             num_events_in_wait_list,
                   const cl_event *    event_wait_list,
                   cl_event *          event) ;

extern  cl_int 
clEnqueueCopyImage(cl_command_queue     command_queue,
                  cl_mem               src_image,
                  cl_mem               dst_image, 
                  const size_t *       src_origin[3],
                  const size_t *       dst_origin[3],
                  const size_t *       region[3], 
                  cl_uint              num_events_in_wait_list,
                  const cl_event *     event_wait_list,
                  cl_event *           event) ;

extern  cl_int 
clEnqueueCopyImageToBuffer(cl_command_queue command_queue,
                          cl_mem           src_image,
                          cl_mem           dst_buffer, 
                          const size_t *   src_origin[3],
                          const size_t *   region[3], 
                          size_t           dst_offset,
                          cl_uint          num_events_in_wait_list,
                          const cl_event * event_wait_list,
                          cl_event *       event) ;

extern  cl_int 
clEnqueueCopyBufferToImage(cl_command_queue command_queue,
                          cl_mem           src_buffer,
                          cl_mem           dst_image, 
                          size_t           src_offset,
                          const size_t *   dst_origin[3],
                          const size_t *   region[3], 
                          cl_uint          num_events_in_wait_list,
                          const cl_event * event_wait_list,
                          cl_event *       event) ;

extern  void * 
clEnqueueMapBuffer(cl_command_queue command_queue,
                  cl_mem           buffer,
                  cl_bool          blocking_map, 
                  cl_map_flags     map_flags,
                  size_t           offset,
                  size_t           cb,
                  cl_uint          num_events_in_wait_list,
                  const cl_event * event_wait_list,
                  cl_event *       event,
                  cl_int *         errcode_ret) ;

extern  void * 
clEnqueueMapImage(cl_command_queue  command_queue,
                 cl_mem            image, 
                 cl_bool           blocking_map, 
                 cl_map_flags      map_flags, 
                 const size_t *    origin[3],
                 const size_t *    region[3],
                 size_t *          image_row_pitch,
                 size_t *          image_slice_pitch,
                 cl_uint           num_events_in_wait_list,
                 const cl_event *  event_wait_list,
                 cl_event *        event,
                 cl_int *          errcode_ret) ;

extern  cl_int 
clEnqueueUnmapMemObject(cl_command_queue command_queue,
                       cl_mem           memobj,
                       void *           mapped_ptr,
                       cl_uint          num_events_in_wait_list,
                       const cl_event *  event_wait_list,
                       cl_event *        event) ;
*/
	
	public void enqueueNDRangeKernel(CLKernel kernel, int workDim, int[] globalOffset, int[] globalSize, int[] localSize, CLEvent[] waitFor)
		{
		int ret=_enqueueNDRangeKernel(id,kernel.id, workDim, globalOffset, globalSize, localSize, waitforToID(waitFor));
		assertSuccess(ret);
		}

	
	
	/*
extern  cl_int 
clEnqueueTask(cl_command_queue  command_queue,
             cl_kernel         kernel,
             cl_uint           num_events_in_wait_list,
             const cl_event *  event_wait_list,
             cl_event *        event) ;

extern  cl_int 
clEnqueueNativeKernel(cl_command_queue  command_queue,
					  void (*user_func)(void *), 
                     void *            args,
                     size_t            cb_args, 
                     cl_uint           num_mem_objects,
                     const cl_mem *    mem_list,
                     const void **     args_mem_loc,
                     cl_uint           num_events_in_wait_list,
                     const cl_event *  event_wait_list,
                     cl_event *        event) ;

extern  cl_int 
clEnqueueMarker(cl_command_queue    command_queue,
               cl_event *          event) ;

extern  cl_int 
clEnqueueWaitForEvents(cl_command_queue command_queue,
                      cl_uint          num_events,
                      const cl_event * event_list) ;

*/
	
	
	public void enqueueBarrier()
		{
		int ret=_enqueueBarrier(id);
		assertSuccess(ret);
		}
	
	}
